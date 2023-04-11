package com.malinowski.diploma.model.wifi

import android.net.wifi.p2p.WifiP2pDevice

sealed class WifiDirectResult {
    class Result(val list: List<WifiP2pDevice>) : WifiDirectResult()
    class Error(val error: Throwable) : WifiDirectResult()
}