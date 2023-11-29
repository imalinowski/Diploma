package com.example.edge_data.internal.models

import kotlinx.serialization.SerialName

internal data class PostTaskRequest(
    @SerialName("device_name") val deviceName: String,
    @SerialName("task_result") val taskResult: String,
    @SerialName("id") val id: Int,
)