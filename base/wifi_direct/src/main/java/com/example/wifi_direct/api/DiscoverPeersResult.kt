package com.example.wifi_direct.api

import android.net.wifi.p2p.WifiP2pDevice

sealed class DiscoverPeersResult {
    class Peers(val peers: List<WifiP2pDevice>) : DiscoverPeersResult()
    class Error(val error: Throwable) : DiscoverPeersResult()
}