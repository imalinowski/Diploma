package com.example.wifi_direct.api

import android.net.wifi.p2p.WifiP2pInfo

sealed class WifiDirectEvents {
    class LogData(val log: String) : WifiDirectEvents()
    class MessageData(val message: Message) : WifiDirectEvents()
    class WifiConnectionChanged(val info: WifiP2pInfo) : WifiDirectEvents()
    class SocketConnectionChanged(val connected: Boolean) : WifiDirectEvents()
    data object PeersChangedAction : WifiDirectEvents()
}