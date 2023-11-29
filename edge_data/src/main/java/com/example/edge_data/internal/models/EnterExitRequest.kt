package com.example.edge_data.internal.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class EnterExitRequest(
    @SerialName("device_name") val deviceName: String,
)