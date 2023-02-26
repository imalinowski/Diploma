package com.malinowski.diploma.model.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WifiDirectCoreImpl @Inject constructor(
    private val context: Context,
    private val intentFilter: IntentFilter,
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
) : WifiDirectCore {

    private val _logFlow = MutableStateFlow("")
    override val logFlow = _logFlow.asStateFlow()

    private val peerFlow = MutableSharedFlow<List<WifiP2pDevice>>()

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

    override suspend fun discoverPeers(): List<WifiP2pDevice> {
        val peerListListener = WifiP2pManager.PeerListListener {
            if (!peerFlow.tryEmit(it.deviceList.toList()))
                error("peers emit error")
        }

        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                manager.requestPeers(channel, peerListListener)
            }

            override fun onFailure(reason: Int) {
                when (reason) {
                    WifiP2pManager.P2P_UNSUPPORTED -> error("P2P_UNSUPPORTED")
                    WifiP2pManager.BUSY -> error("BUSY")
                    WifiP2pManager.ERROR -> error("ERROR")
                }
            }
        })

        return peerFlow.first()
    }

    override suspend fun sendMessage(id: String) {
        TODO("Not yet implemented")
    }

}