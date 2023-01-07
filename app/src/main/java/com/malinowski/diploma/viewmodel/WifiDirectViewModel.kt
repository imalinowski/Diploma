package com.malinowski.diploma.viewmodel

import android.app.Application
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.malinowski.diploma.model.wifi.WifiBroadcastReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class WifiDirectState(
    val logText: String = ""
)

class WifiDirectViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WifiDirectState())
    private val _uiIntents = MutableStateFlow(String)
    val uiState = _uiState.asStateFlow()
    val uiIntents = _uiIntents.asStateFlow()
    private val context by lazy {
        getApplication<Application>().applicationContext
        // TODO add DI
    }

    private val intentFilter by lazy {
        IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
    }
    private val manager: WifiP2pManager by lazy {
        context.getSystemService(AppCompatActivity.WIFI_P2P_SERVICE) as WifiP2pManager
    }
    private val channel: WifiP2pManager.Channel by lazy {
        manager.initialize(context, context.mainLooper, null)
    }
    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)
        }

        if (peers.isEmpty()) {
            appendText("No devices found")
            return@PeerListListener
        } else {
            appendText("Peers : ${peers.joinToString()}")
        }
    }
    private val receiver: WifiBroadcastReceiver by lazy {
        WifiBroadcastReceiver(
            requestPeers = { manager.requestPeers(channel, peerListListener) },
            appendText = { appendText(it) }
        )
    }

    private val peers = mutableListOf<WifiP2pDevice>()

    private fun appendText(text: String) {
        _uiState.value = _uiState.value.let { state ->
            state.copy(
                logText = state.logText + "\n ${System.currentTimeMillis()}: $text"
            )
        }
    }

    fun searchForDevices() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                try {
                    manager.requestPeers(channel, peerListListener)
                } catch (e: SecurityException) {
                    Log.e("RASPBERRY", "Permission denied")
                }
                appendText("discoverPeers success")
            }

            override fun onFailure(p0: Int) {
                when (p0) {
                    WifiP2pManager.P2P_UNSUPPORTED -> appendText("P2P_UNSUPPORTED ")
                    WifiP2pManager.BUSY -> appendText("BUSY ")
                    WifiP2pManager.ERROR -> appendText("ERROR ")
                }
                appendText("discoverPeers failed")
            }
        })
        appendText("searching for devices ...")
    }

    fun registerReceiver() {
        context.registerReceiver(receiver, intentFilter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(receiver)
    }
}