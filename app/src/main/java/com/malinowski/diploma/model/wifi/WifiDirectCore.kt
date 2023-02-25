package com.malinowski.diploma.model.wifi

import android.net.wifi.p2p.WifiP2pDevice

interface WifiDirectCore {
    fun registerReceiver()

    fun unRegisterReceiver()

    suspend fun discoverPeers(): List<WifiP2pDevice>

    suspend fun sendMessage(id: String)
}