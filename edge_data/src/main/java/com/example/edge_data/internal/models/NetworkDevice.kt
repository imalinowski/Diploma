package com.example.edge_data.internal.models

import kotlinx.serialization.SerialName

internal data class NetworkDevice(
    @SerialName("id") val id: Int,
    @SerialName("device_name") val deviceName: String,
    @SerialName("status") val status: String
)