package com.malinowski.chat.internal.model

import android.net.wifi.p2p.WifiP2pInfo
import com.example.wifi_direct.api.Message

data class WifiDirectUiState(
    val logText: String = "",
    val peers: List<WifiDirectPeer> = emptyList(),
    val permissionsGranted: Boolean = false,
    val messages: List<Message> = emptyList(),
    val wifiConnectionInfo: WifiP2pInfo = WifiP2pInfo(),
    val chatConnectionInfo: Boolean = false
)