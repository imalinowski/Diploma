package com.example.edge_data.internal.models.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

internal data class PostTaskRequest(
    @SerialName("device_name") val deviceName: String,
    @SerialName("task_result") val taskResult: String,
    @SerialName("id") val id: Int,
)