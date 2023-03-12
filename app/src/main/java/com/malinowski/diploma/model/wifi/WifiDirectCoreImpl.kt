package com.malinowski.diploma.model.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import com.malinowski.diploma.model.wifi.WifiDirectCoreImpl.WifiDirectResult.Error
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

    private val peerFlow = flow {

        val channel = Channel<WifiDirectResult>()

        val peerListListener = WifiP2pManager.PeerListListener {
            val peers = it.deviceList.toList()
            _logFlow.value = "\n Peers : ${peers.joinToString("\n")}"
            launch {
                channel.send(WifiDirectResult.Result(peers))
            }
        }

        manager.discoverPeers(managerChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                manager.requestPeers(managerChannel, peerListListener)
            }

            override fun onFailure(reason: Int) {
                val message = when (reason) {
                    WifiP2pManager.P2P_UNSUPPORTED -> "P2P_UNSUPPORTED"
                    WifiP2pManager.BUSY -> "BUSY"
                    else -> "ERROR"
                }
                launch {
                    channel.send(Error(Throwable(message)))
                }
            }
        })

        emit(channel.receive())
    }.catch {
        emit(Error(it))
    }.shareIn( // for battery life performance
        this,
        SharingStarted.WhileSubscribed(replayExpirationMillis = CACHE_EXPIRATION_TIME),
        replay = 1
    )

    private val receiver: WifiBroadcastReceiver by lazy {
        WifiBroadcastReceiver(
            requestPeers = { },
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

    override suspend fun sendMessage(id: String) {
        TODO("Not yet implemented")
    }

    sealed class WifiDirectResult {
        class Result(val list: List<WifiP2pDevice>) : WifiDirectResult()
        class Error(val error: Throwable) : WifiDirectResult()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000L
    }
}