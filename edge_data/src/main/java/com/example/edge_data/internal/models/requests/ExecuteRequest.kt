package com.example.edge_data.internal.models.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ExecuteRequest(
    @SerialName("id") val id: Int,
    @SerialName("device_name") val deviceName: String,
    @SerialName("task") val content: String
)