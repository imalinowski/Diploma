package com.malinowski.diploma.model.wifi

import android.net.wifi.p2p.WifiP2pInfo
import com.malinowski.diploma.model.Message

sealed class WifiDirectData {
    class LogData(val log: String) : WifiDirectData()
    class MessageData(val message: Message) : WifiDirectData()
    class WifiConnectionChanged(val info: WifiP2pInfo) : WifiDirectData()
    class SocketConnectionChanged(val connected: Boolean) : WifiDirectData()
}