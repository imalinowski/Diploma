package com.malinowski.diploma.model.wifi

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class WifiDirectCore @Inject constructor(
    private val context: Context,
    private val intentFilter: IntentFilter,
    private val manager: WifiP2pManager,
    private val channel: Channel
) {
    //todo modify with custom events
    private val _logFlow = MutableStateFlow("")
    val stateFlow = _logFlow.asStateFlow()

    private val peers = mutableListOf<WifiP2pDevice>()
    private val _peerFlow = MutableStateFlow(listOf<WifiP2pDevice>())
    val peerFlow = _peerFlow.asStateFlow()

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)
        }
        val testDevice = WifiP2pDevice().apply { deviceName = "test1sdkjfkjsdnfjksdfnkjknjsdjnfk" }
        peers.addAll(listOf(testDevice, testDevice, testDevice))

        if (peers.isEmpty()) {
            _logFlow.value = "No devices found"
            return@PeerListListener
        } else {
            _logFlow.value = "\n Peers : ${peers.joinToString("\n")}"
        }

        _peerFlow.value = peers
    }

    private val receiver: WifiBroadcastReceiver by lazy {
        WifiBroadcastReceiver(
            requestPeers = { manager.requestPeers(channel, peerListListener) },
            appendText = { _logFlow.value = it }
        )
    }

    fun registerReceiver() {
        context.registerReceiver(receiver, intentFilter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(receiver)
    }

    fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                try {
                    manager.requestPeers(channel, peerListListener)
                } catch (e: SecurityException) {
                    Log.e("RASPBERRY", "Permission denied")
                }
                _logFlow.value = "discoverPeers success"
            }

            override fun onFailure(p0: Int) {
                when (p0) {
                    WifiP2pManager.P2P_UNSUPPORTED -> _logFlow.value = "P2P_UNSUPPORTED"
                    WifiP2pManager.BUSY -> _logFlow.value = "BUSY "
                    WifiP2pManager.ERROR -> _logFlow.value = "ERROR "
                }
                _logFlow.value = "discoverPeers failed"
            }
        })
        _logFlow.value = "searching for devices ..."
    }

    companion object {
        val WIFI_CORE_PERMISSIONS by lazy {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
            )
        }
        val WIFI_CORE_PERMISSIONS_13 by lazy {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
            } else arrayOf()
        }

    }
}