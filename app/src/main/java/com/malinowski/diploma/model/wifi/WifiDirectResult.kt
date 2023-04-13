package com.malinowski.diploma.model.wifi

import android.net.wifi.p2p.WifiP2pDevice

sealed class WifiDirectResult {
    class Peers(val peer: List<WifiP2pDevice>) : WifiDirectResult()
    object Success : WifiDirectResult()
    class Error(val error: Throwable) : WifiDirectResult()
}