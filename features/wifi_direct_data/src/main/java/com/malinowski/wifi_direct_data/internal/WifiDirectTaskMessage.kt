package com.malinowski.wifi_direct_data.internal

data class WifiDirectTaskMessage(
    val type: WifiDirectTaskMessageType,
    val content: String
)

enum class WifiDirectTaskMessageType { Task, Result }