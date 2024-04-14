package com.malinowski.chat.internal.model.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager

private const val TAG = "RASPBERRY"

class WifiBroadcastReceiver(
    private val requestPeers: () -> Unit,
    private val connect: () -> Unit,
    private val log: (String) -> Unit,
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        log("Action: ${intent.action}\n")
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                val isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                log("WifiP2PEnabled -> $isWifiP2pEnabled")
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                requestPeers()
            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val networkInfo: NetworkInfo? =
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
                if (networkInfo?.isConnected == true)
                    connect()
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val data: WifiP2pDevice? =
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                log(data?.deviceName ?: "unknown")
            }
        }
    }
}