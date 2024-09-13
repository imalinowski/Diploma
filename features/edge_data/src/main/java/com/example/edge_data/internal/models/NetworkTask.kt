package com.example.edge_data.internal.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkTask(
    @SerialName("id")
    val id: Int,
    @SerialName("content")
    val content: String,
    @SerialName("task_result")
    var taskResult: String? = null,
)

@Serializable
internal data class NetworkTaskContent(
    @SerialName("task_name")
    val taskName: String,
    @SerialName("parent_id")
    val parentId: Int,
    @SerialName("device_name")
    val deviceName: String,
    @SerialName("params")
    val params: String,
)

@Serializable
internal data class NetworkTaskResult(
    @SerialName("device_name")
    val deviceName: String,
    @SerialName("task_result")
    val taskResult: String,
)
