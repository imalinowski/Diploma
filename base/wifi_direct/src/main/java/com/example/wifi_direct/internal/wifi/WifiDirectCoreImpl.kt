package com.example.wifi_direct.internal.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.CONNECTION_REQUEST_ACCEPT
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.util.Log
import com.example.entities.Logs
import com.example.entities.getTime
import com.example.wifi_direct.api.DiscoverPeersResult
import com.example.wifi_direct.api.Message
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.api.WifiDirectEvents
import com.example.wifi_direct.api.WifiDirectEvents.LogData
import com.example.wifi_direct.api.WifiDirectEvents.PeersChangedAction
import com.example.wifi_direct.api.WifiDirectEvents.WifiConnectionChanged
import com.example.wifi_direct.internal.exceptions.WifiDirectErrorHandlerFactory
import com.example.wifi_direct.internal.exceptions.markDeviceName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

const val POSSIBLE_ERROR_SOLUTIONS = """
Check following : 
- location services must be enabled for wifi direct to work 
"""

private const val CACHE_EXPIRATION_TIME = 1000L

@Singleton
class WifiDirectCoreImpl
@Inject constructor(
    private val context: Context,
    private val intentFilter: IntentFilter,
    private val manager: WifiP2pManager,
    private val managerChannel: WifiP2pManager.Channel,
    private val logs: Logs,
    private val errorHandlerFactory: WifiDirectErrorHandlerFactory
) : WifiDirectCore, CoroutineScope {

    init {
        setDeviceName()
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private val _dataFlow = MutableSharedFlow<WifiDirectEvents?>(extraBufferCapacity = 2)
    override val dataFlow = _dataFlow

    private var peers: List<WifiP2pDevice> = emptyList()

    private var wifiDirectSocket: WifiDirectSocket? = null

    private var connectionInfo = WifiP2pInfo()

    private val receiver: WifiBroadcastReceiver by lazy {
        WifiBroadcastReceiver(
            requestPeers = { sendToDataFlow(PeersChangedAction) },
            connect = { manager.requestConnectionInfo(managerChannel, connectInfoListener) },
            log = { sendToDataFlow(LogData(it)) }
        )
    }

    private fun sendToDataFlow(event: WifiDirectEvents) {
        if (event is LogData) {
            logs.logData(event.log)
        }
        launch {
            _dataFlow.emit(event)
        }
    }

    @SuppressLint("MissingPermission")
    private val peerFlow = flow {
        val channel = Channel<DiscoverPeersResult>()

        val peerListListener = PeerListListener {
            peers = it.deviceList.toList()
            sendToDataFlow(LogData("\nPeers ${peers.size} \n ${peers.joinToString("\n")}"))
            launch { channel.send(DiscoverPeersResult.Peers(peers)) }
        }

        setDeviceName()

        manager.discoverPeers(managerChannel, errorHandlerFactory.actionListener(
            onFail = { _, it -> launch { channel.send(DiscoverPeersResult.Error(Exception(it))) } },
        ) { manager.requestPeers(managerChannel, peerListListener) }
        )

        emit(channel.receive())
    }.catch {
        emit(DiscoverPeersResult.Error(it))
    }.shareIn( // for battery life performance
        this,
        SharingStarted.WhileSubscribed(replayExpirationMillis = CACHE_EXPIRATION_TIME),
        replay = 1
    )

    private val connectInfoListener: (WifiP2pInfo?) -> Unit = { info ->
        sendToDataFlow(WifiConnectionChanged(info ?: WifiP2pInfo()))
        val hostAddress = info?.groupOwnerAddress?.hostAddress
        if (info?.groupFormed == true && hostAddress != null) {
            Log.i("RASPBERRY", "connection listener")
            connectionInfo = info
            createWifiDirectSocket(info.isGroupOwner, hostAddress)
        }
    }

    private fun createWifiDirectSocket(
        isGroupOwner: Boolean,
        hostAddress: String
    ) {
        // защита от спама WiFiBroadcastReciever
        if (hostAddress == wifiDirectSocket?.hostAddress) return
        wifiDirectSocket?.shutDown(restart = false)
        wifiDirectSocket = if (isGroupOwner) {
            WifiDirectServer(hostAddress)
        } else {
            WifiDirectClient(hostAddress)
        }.apply {
            log = { log -> sendToDataFlow(LogData(log)) }
            onConnectionChanged = { sendToDataFlow(WifiDirectEvents.SocketConnectionChanged(it)) }
            onReceive = { message ->
                Log.i("RASPBERRY", "send message from WiFiDirect $message")
                sendToDataFlow(
                    WifiDirectEvents.MessageData(
                        Message(text = message, author = hostAddress, time = getTime("hh:mm:ss.SSS"))
                    )
                )
            }
        }
    }

    @SuppressLint("NewApi", "MissingPermission")
    // почему не используется connect ?
    private fun connectFlow(deviceName: String) = flow {
        val channel = Channel<Boolean>()

        val device = peers.find { it.deviceAddress == deviceName }
            ?: throw IllegalStateException("device with address $deviceName Not Found!")

        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        val actionListener = errorHandlerFactory.actionListener(
            onSuccess = { launch { channel.send(true) } },
            onFail = { code, _ ->
                launch { channel.send(code == CONNECTION_REQUEST_ACCEPT) }
            }
        )

//        manager.connect(managerChannel, config, actionListener)
        manager.createGroup(managerChannel, config, actionListener)

        emit(channel.receive())
    }.shareIn(
        this,
        SharingStarted.WhileSubscribed(replayExpirationMillis = CACHE_EXPIRATION_TIME * 10),
        replay = 1
    ).onEach { success ->
        if (success) {
            Log.i("RASPBERRY", "request connection from $deviceName")
            manager.requestConnectionInfo(managerChannel, connectInfoListener)
        }
    }

    private fun disconnectFlow() = flow {
        val channel = Channel<Boolean>()
        val actionListener = errorHandlerFactory.actionListener(
            onSuccess = { launch { channel.send(true) } },
            onFail = { _, _ -> launch { channel.send(false) } }
        )
        manager.cancelConnect(managerChannel, actionListener)
        emit(channel.receive())
    }

    override fun registerReceiver() {
        context.registerReceiver(receiver, intentFilter)
    }

    private fun setDeviceName() {
//        peers.first()
        sendToDataFlow(LogData("Trying to clearLocalServices"))
        manager.clearLocalServices(managerChannel, errorHandlerFactory.actionListener {
            val name = "${Random.nextInt(10)}_RASP"
            sendToDataFlow(LogData("Trying to set device name to $name"))
            manager.markDeviceName(managerChannel, name, errorHandlerFactory.actionListener {
                sendToDataFlow(LogData("set name succeed"))
            })
//            sendToDataFlow(LogData("Trying to call another function via reflection"))
//            manager.reflectDiscoverPeers(managerChannel, name, errorHandlerFactory.actionListener {
//                sendToDataFlow(LogData("call function via reflection succeed"))
//            })
        })
    }

    override fun unRegisterReceiver() {
        context.unregisterReceiver(receiver)
    }

    override fun getConnectionInfo(): WifiP2pInfo {
        return connectionInfo
    }

    override suspend fun discoverPeers(): DiscoverPeersResult {
        sendToDataFlow(LogData("Searching for devices ..."))
        return withContext(Dispatchers.Default) {
            peerFlow.first()
        }
    }

    override suspend fun connect(address: String): Boolean {
        return connectFlow(address)
            .onEach { sendToDataFlow(LogData("connect to $address ... $it")) }
            .first()
    }

    override suspend fun connectCancel(): Boolean { // unregisterReciever?
        return disconnectFlow()
            .onEach { sendToDataFlow(LogData("disConnect")) }
            .first()
    }

    override suspend fun sendMessage(message: String) {
        wifiDirectSocket?.write(message)
            ?: throw IllegalStateException("wifiDirectSocket is null")
    }
}