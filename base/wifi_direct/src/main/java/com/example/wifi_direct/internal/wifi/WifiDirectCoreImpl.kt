package com.example.wifi_direct.internal.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pManager.BUSY
import android.net.wifi.p2p.WifiP2pManager.CONNECTION_REQUEST_ACCEPT
import android.net.wifi.p2p.WifiP2pManager.NO_SERVICE_REQUESTS
import android.net.wifi.p2p.WifiP2pManager.P2P_UNSUPPORTED
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

const val POSSIBLE_ERROR_SOLUTIONS = """
Check following : 
- location services must be enabled for wifi direct to work 
"""

@Singleton
class WifiDirectCoreImpl
@Inject constructor(
    private val context: Context,
    private val intentFilter: IntentFilter,
    private val manager: WifiP2pManager,
    private val managerChannel: WifiP2pManager.Channel,
    private val logs: Logs
) : WifiDirectCore, CoroutineScope {

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

        manager.discoverPeers(managerChannel, actionListener(
            onSuccess = { manager.requestPeers(managerChannel, peerListListener) },
            onFail = { _, it -> launch { channel.send(DiscoverPeersResult.Error(Exception(it))) } }
        ))

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

        val actionListener = actionListener(
            onSuccess = { launch { channel.send(true) } },
            onFail = { code, _ ->
                launch { channel.send(code == CONNECTION_REQUEST_ACCEPT) }
            }
        )

        manager.connect(managerChannel, config, actionListener)

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
        val actionListener = actionListener(
            onSuccess = { launch { channel.send(true) } },
            onFail = { _, _ -> launch { channel.send(false) } }
        )
        manager.cancelConnect(managerChannel, actionListener)
        emit(channel.receive())
    }

    override fun registerReceiver() {
        context.registerReceiver(receiver, intentFilter)
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

    private fun actionListener(
        onSuccess: () -> Unit,
        onFail: (Int, String) -> Unit
    ) = object : ActionListener {
        override fun onSuccess() {
            onSuccess()
        }

        override fun onFailure(reason: Int) {
            val message = when (reason) {
                P2P_UNSUPPORTED -> "P2P_UNSUPPORTED"
                BUSY -> "BUSY"
                NO_SERVICE_REQUESTS -> "NO_SERVICE_REQUESTS"
                else -> "ERROR! $POSSIBLE_ERROR_SOLUTIONS"
            }
            Log.e("RASPBERRY", "Error : $reason $message")
            sendToDataFlow(LogData("Error : $reason $message"))
            onFail(reason, message)
        }
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000L
    }
}