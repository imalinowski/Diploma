package com.malinowski.wifi_direct_data.internal

import kotlinx.serialization.Serializable

@Serializable
data class WifiDirectTaskMessage(
    val taskId: Int,
    val type: WifiDirectTaskMessageType,
    val content: String
)

enum class WifiDirectTaskMessageType { Task, Result }