package com.malinowski.diploma.model.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import com.malinowski.diploma.model.wifi.WifiDirectCoreImpl.WifiDirectResult.Error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class WifiDirectCoreImpl @Inject constructor(
    private val context: Context,
    private val intentFilter: IntentFilter,
    private val manager: WifiP2pManager,
    private val managerChannel: WifiP2pManager.Channel
) : WifiDirectCore {

    private val _logFlow = MutableStateFlow("")
    override val logFlow = _logFlow.asStateFlow()

    private val peerFlow = callbackFlow {

        val sendData: suspend (data: WifiDirectResult) -> Unit = { send(it) }
        val closeChannel = { close() }
        val context = coroutineContext

        val peerListListener = WifiP2pManager.PeerListListener {
            val peers = it.deviceList.toList()
            _logFlow.value = "\n Peers : ${peers.joinToString("\n")}"
            runBlocking(context) {
                sendData(WifiDirectResult.Result(peers)) //todo make more clever
            }
            closeChannel()
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
                this@callbackFlow.trySend(Error(Throwable(message)))
            }
        })
        awaitClose()
    }.catch {
        emit(Error(it))
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
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
        return peerFlow.first()
    }

    override suspend fun sendMessage(id: String) {
        TODO("Not yet implemented")
    }

    sealed class WifiDirectResult {
        class Result(val list: List<WifiP2pDevice>) : WifiDirectResult()
        class Error(val error: Throwable) : WifiDirectResult()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 10000L
    }
}