package com.malinowski.diploma.model.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log


private const val TAG = "RASPBERRY"

class WifiBroadcastReceiver(
    private val requestPeers: () -> Unit,
    private val appendText: (String) -> Unit,
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                appendText("$TAG : $log ")
            }
        }
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wi-Fi Direct mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                val isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                appendText("WifiP2PEnabled -> $isWifiP2pEnabled")
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                try {
                    requestPeers()
                } catch (e: SecurityException) {
                    Log.e("RASPBERRY", "Permission denied")
                }
                Log.d("RASPBERRY", "P2P peers changed")
                // The peer list has changed! We should probably do something about
                // that.
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

                // Connection state changed! We should probably do something about
                // that.

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val data =
                    (intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as? WifiP2pDevice)
                appendText(data?.deviceName ?: "unknown")
            }
        }
    }
}