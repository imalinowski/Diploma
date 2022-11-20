package com.malinowski.diploma.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.core.content.ContextCompat.getSystemService


class WifiAccessPoint {
    var connectedDevices: Int = 0
        private set

    val intentFilter = IntentFilter()
    lateinit var channel: WifiP2pManager.Channel
    lateinit var manager: WifiP2pManager

    suspend fun createWAP(): Boolean {
        return true
    }

    fun onCreate(context: Context, mainLooper: Looper){
        // Indicates a change in the Wi-Fi Direct status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

        // Indicates the state of Wi-Fi Direct connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(context, mainLooper, null)
    }
}