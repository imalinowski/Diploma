package com.malinowski.diploma.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.widget.Toast
import com.malinowski.diploma.WifiDirectActivity


private const val TAG = "RASPBERRY"

class WifiBroadcastReceiver(
    private val peerListListener: WifiP2pManager.PeerListListener,
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: WifiDirectActivity,
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wi-Fi Direct mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                activity.appendText("WifiP2PEnabled -> ${activity.isWifiP2pEnabled}")
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                try {
                    manager.requestPeers(channel, peerListListener)
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
                activity.appendText(data?.deviceName ?: "unknown")
            }
        }
    }
}