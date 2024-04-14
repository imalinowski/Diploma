package com.malinowski.chat.internal.model.wifi

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
import com.malinowski.chat.internal.ext.getTime
import com.malinowski.chat.internal.model.Message
import com.malinowski.chat.internal.model.wifi.WifiDirectData.LogData
import com.malinowski.chat.internal.model.wifi.WifiDirectData.WifiConnectionChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WifiDirectCoreImpl @Inject constructor(
    private val context: Context,
    private val intentFilter: IntentFilter,
    private val manager: WifiP2pManager,
    private val managerChannel: WifiP2pManager.Channel
) : WifiDirectCore, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private val _dataFlow: MutableStateFlow<WifiDirectData?> = MutableStateFlow(null)
    override val dataFlow = _dataFlow.asStateFlow()

    private var peers: List<WifiP2pDevice> = emptyList()

    private var wifiDirectSocket: WifiDirectSocket? = null

    private var connectionInfo = WifiP2pInfo()

    private val receiver: WifiBroadcastReceiver by lazy {
        WifiBroadcastReceiver(
            requestPeers = { /* called when CHANGED_ACTION */ },
            connect = { manager.requestConnectionInfo(managerChannel, connectInfoListener) },
            log = { _dataFlow.value = LogData(it) }
        )
    }

    @SuppressLint("MissingPermission")
    private val peerFlow = flow {
        val channel = Channel<WifiDirectResult>()

        val peerListListener = PeerListListener {
            peers = it.deviceList.toList()
            _dataFlow.value = LogData("\n Peers : ${peers.joinToString("\n")}")
            launch { channel.send(WifiDirectResult.Peers(peers)) }
        }

        manager.discoverPeers(managerChannel, actionListener(
            onSuccess = { manager.requestPeers(managerChannel, peerListListener) },
            onFail = { _, it -> launch { channel.send(WifiDirectResult.Error(Exception(it))) } }
        ))

        emit(channel.receive())
    }.catch {
        emit(WifiDirectResult.Error(it))
    }.shareIn( // for battery life performance
        this,
        SharingStarted.WhileSubscribed(replayExpirationMillis = CACHE_EXPIRATION_TIME),
        replay = 1
    )

    private val connectInfoListener: (WifiP2pInfo?) -> Unit = { info ->
        _dataFlow.value = WifiConnectionChanged(info ?: WifiP2pInfo())
        if (info != null && info.groupFormed) {
            connectionInfo = info
            val inetAddress = info.groupOwnerAddress.hostAddress!!
            wifiDirectSocket?.shutDown(restart = false)
            wifiDirectSocket = if (info.isGroupOwner) {
                WifiDirectServer()
            } else {
                WifiDirectClient(inetAddress)
            }.apply {
                log = { log ->
                    _dataFlow.value = LogData(log)
                }
                onReceive = { message ->
                    _dataFlow.value = WifiDirectData.MessageData(
                        Message(text = message, author = inetAddress, time = getTime("hh:mm:ss.SSS"))
                    )
                }
                onConnectionChanged = {
                    _dataFlow.value = WifiDirectData.SocketConnectionChanged(it)
                }
            }
        }
    }

    @SuppressLint("NewApi", "MissingPermission")
    private fun connectFlow(connect: Boolean, deviceName: String) = flow {
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
            manager.requestConnectionInfo(managerChannel, connectInfoListener)
        }
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

    override suspend fun discoverPeers(): WifiDirectResult {
        _dataFlow.value = LogData("searching for devices ...")
        return withContext(Dispatchers.Default) {
            peerFlow.first()
        }
    }

    override suspend fun connect(address: String): Boolean {
        return connectFlow(true, address)
            .onEach { _dataFlow.value = LogData("connect to $address ... $it") }
            .first()
    }

    override suspend fun connectCancel(address: String): Boolean {
        return connectFlow(false, address)
            .onEach { _dataFlow.value = LogData("disConnect from $address ... $it") }
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
                CONNECTION_REQUEST_ACCEPT -> "CONNECTION_REQUEST_ACCEPT"
                P2P_UNSUPPORTED -> "P2P_UNSUPPORTED"
                BUSY -> "BUSY"
                NO_SERVICE_REQUESTS -> "NO_SERVICE_REQUESTS"
                else -> "ERROR"
            }
            Log.e("RASPBERRY", "Error : $reason $message")
            onFail(reason, message)
        }
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000L
    }
}