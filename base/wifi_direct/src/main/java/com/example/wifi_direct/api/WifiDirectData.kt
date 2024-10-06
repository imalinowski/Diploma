package com.example.wifi_direct.api

import android.net.wifi.p2p.WifiP2pInfo

sealed class WifiDirectData {
    class LogData(val log: String) : WifiDirectData()
    class MessageData(val message: Message) : WifiDirectData()
    class WifiConnectionChanged(val info: WifiP2pInfo) : WifiDirectData()
    class SocketConnectionChanged(val connected: Boolean) : WifiDirectData()
}