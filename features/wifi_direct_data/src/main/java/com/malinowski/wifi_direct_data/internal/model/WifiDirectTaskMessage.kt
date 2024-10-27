package com.malinowski.wifi_direct_data.internal.model

import kotlinx.serialization.Serializable

@Serializable
data class WifiDirectTaskMessage(
    val type: WifiDirectTaskMessageType,
    val content: String
)

@Serializable
sealed interface WifiDirectTaskMessageType {
    @Serializable
    data object Syn : WifiDirectTaskMessageType

    @Serializable
    data object Ack : WifiDirectTaskMessageType

    @Serializable
    data class Task(
        val taskId: Int,
    ) : WifiDirectTaskMessageType

    @Serializable
    data object Result : WifiDirectTaskMessageType
}