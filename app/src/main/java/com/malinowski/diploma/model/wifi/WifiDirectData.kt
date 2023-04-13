package com.malinowski.diploma.model.wifi

import com.malinowski.diploma.model.Message

sealed class WifiDirectData {
    class LogData(val log: String) : WifiDirectData()
    class MessageData(val message: Message) : WifiDirectData()
}