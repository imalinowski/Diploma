package com.malinowski.diploma.model.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.*
import android.util.Log
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

    private val _logFlow = MutableStateFlow("")
    override val logFlow = _logFlow.asStateFlow()

    private var peers: List<WifiP2pDevice> = emptyList()

    private val peerFlow = flow {
        val channel = Channel<WifiDirectResult>()

        val peerListListener = PeerListListener {
            peers = it.deviceList.toList()
            _logFlow.value = "\n Peers : ${peers.joinToString("\n")}"
            launch { channel.send(WifiDirectResult.Result(peers)) }
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
                launch {
                    if (code == CONNECTION_REQUEST_ACCEPT)
                        channel.send(true)
                    else channel.send(false)
                }
            }
        )

        if (connect) {
            manager.connect(managerChannel, config, actionListener)
        } else {
            manager.cancelConnect(managerChannel, actionListener)
        }

        emit(channel.receive())
    }.shareIn(
        this,
        SharingStarted.WhileSubscribed(replayExpirationMillis = CACHE_EXPIRATION_TIME),
        replay = 1
    )

    private val receiver: WifiBroadcastReceiver by lazy {
        WifiBroadcastReceiver(
            requestPeers = { /* called when CHANGED_ACTION */ },
            log = { _logFlow.value = it }
        )
    }

    override fun registerReceiver() {
        context.registerReceiver(receiver, intentFilter)
    }

    override fun unRegisterReceiver() {
        context.unregisterReceiver(receiver)
    }

    override suspend fun discoverPeers(): WifiDirectResult {
        _logFlow.value = "searching for devices ..."
        return withContext(Dispatchers.Default) {
            peerFlow.first()
        }
    }

    override suspend fun connect(address: String): Boolean {
        _logFlow.value = "connect to $address ..."
        return withContext(Dispatchers.Default) {
            connectFlow(true, address).first()
        }
    }

    override suspend fun connectCancel(address: String): Boolean {
        _logFlow.value = "connect to $address ..."
        return withContext(Dispatchers.Default) {
            connectFlow(false, address).first()
        }
    }

    override suspend fun sendMessage() {
        TODO("Not yet implemented")
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
                CONNECTION_REQUEST_ACCEPT -> "CONNECTION_REQUEST_ACCEPT"
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