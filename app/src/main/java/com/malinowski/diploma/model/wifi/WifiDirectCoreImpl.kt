package com.malinowski.diploma.model.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.*
import android.util.Log
import com.malinowski.diploma.model.wifi.WifiDirectData.LogData
import com.malinowski.diploma.model.wifi.WifiDirectData.MessageData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
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

    private val _logFlow: MutableStateFlow<WifiDirectData?> = MutableStateFlow(null)
    override val logFlow = _logFlow.asStateFlow()

    private var peers: List<WifiP2pDevice> = emptyList()

    private var server: WifiDirectServer? = null
    private var client: WifiDirectClient? = null

    private val peerFlow = flow {
        val channel = Channel<WifiDirectResult<List<WifiP2pDevice>>>()

        val peerListListener = PeerListListener {
            peers = it.deviceList.toList()
            _logFlow.value = LogData("\n Peers : ${peers.joinToString("\n")}")
            launch { channel.send(WifiDirectResult.Success(peers)) }
        }

        manager.discoverPeers(managerChannel, actionListener(
            onSuccess = { manager.requestPeers(managerChannel, peerListListener) },
            onFail = { _, it -> launch { channel.send(WifiDirectResult.Error(Throwable(it))) } }
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
        if (info != null) {
            _logFlow.value = LogData(info.toString())

            val inetAddress = info.groupOwnerAddress
            if (info.groupFormed && info.isGroupOwner) {
                server = WifiDirectServer {
                    _logFlow.value = MessageData(it)
                }
            } else if (info.groupFormed && info.isGroupOwner) {
                client = WifiDirectClient(inetAddress.hostAddress!!) {
                    _logFlow.value = MessageData(it)
                }
            }
        }
    }

    private fun connectFlow(connect: Boolean, deviceName: String) = flow {
        val channel = Channel<WifiDirectResult<Boolean>>()

        val device = peers.find { it.deviceAddress == deviceName }
            ?: throw IllegalStateException("device with address $deviceName Not Found!")

        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        val actionListener = actionListener(
            onSuccess = { launch { channel.send(WifiDirectResult.Success(true)) } },
            onFail = { code, _ ->
                launch {
                    channel.send(WifiDirectResult.Success(code == 0 && connect))
                }
            }
        )

        if (connect) {
            manager.connect(managerChannel, config, actionListener)
        } else {
            manager.cancelConnect(managerChannel, actionListener)
        }
        manager.requestConnectionInfo(managerChannel, connectInfoListener)

        emit(channel.receive())
    }.shareIn(
        this,
        SharingStarted.WhileSubscribed(replayExpirationMillis = CACHE_EXPIRATION_TIME),
        replay = 1
    )

    private val receiver: WifiBroadcastReceiver by lazy {
        WifiBroadcastReceiver(
            requestPeers = { /* called when CHANGED_ACTION */ },
            log = { _logFlow.value = LogData(it) }
        )
    }

    override fun registerReceiver() {
        context.registerReceiver(receiver, intentFilter)
    }

    override fun unRegisterReceiver() {
        context.unregisterReceiver(receiver)
    }

    override suspend fun discoverPeers(): WifiDirectResult<List<WifiP2pDevice>> {
        _logFlow.value = LogData("searching for devices ...")
        return withContext(Dispatchers.Default) {
            peerFlow.first()
        }
    }

    override suspend fun connect(address: String): WifiDirectResult<Boolean> {
        _logFlow.value = LogData("connect to $address ...")
        return withContext(Dispatchers.Default) {
            connectFlow(true, address).first()
        }
    }

    override suspend fun connectCancel(address: String): WifiDirectResult<Boolean> {
        _logFlow.value = LogData("disConnect from $address ...")
        return withContext(Dispatchers.Default) {
            connectFlow(false, address).first()
        }
    }

    override suspend fun sendMessage(message: String) {
        client?.write(message)
        server?.write(message)
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