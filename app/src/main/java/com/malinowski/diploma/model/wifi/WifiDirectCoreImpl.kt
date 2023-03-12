package com.malinowski.diploma.model.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
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

    private val job: Job = Job() // very smart shit
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private val _logFlow = MutableStateFlow("")
    override val logFlow = _logFlow.asStateFlow()

    private val peerFlow = flow {

        val channel = Channel<WifiDirectResult>(capacity = Channel.RENDEZVOUS)

        val peerListListener = WifiP2pManager.PeerListListener {
            val peers = it.deviceList.toList()
            _logFlow.value = "\n Peers : ${peers.joinToString("\n")}"
            launch {
                Log.i(
                    "RASPBERRY",
                    "thread name :${Thread.currentThread().name} > peers : ${peers.size}"
                )
                channel.send(WifiDirectResult.Result(peers)) //todo make more clever
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
                    Log.i("RASPBERRY", "error thread name :${Thread.currentThread().name}")
                    channel.send(Error(Throwable(message)))
                }
            }
        })

        emit(channel.receive())
    }.catch {
        emit(Error(it))
    }

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

    override suspend fun discoverPeers(): WifiDirectResult = coroutineScope {
        _logFlow.value = "searching for devices ..."
        withContext(Dispatchers.Default) {
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
        private const val CACHE_EXPIRATION_TIME = 100L
    }
}