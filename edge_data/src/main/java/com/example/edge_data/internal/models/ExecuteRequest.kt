package com.example.edge_data.internal.models

import kotlinx.serialization.SerialName

internal data class ExecuteRequest(
    @SerialName("device_name") val deviceName: String,
    @SerialName("task") val task: NetworkTask
)