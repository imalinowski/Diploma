package com.example.edge_domain.api.dependecies.data

import com.example.entities.EdgeDevice
import com.example.entities.tasks.EdgeResult
import com.example.entities.tasks.EdgeSubTaskBasic

sealed interface EdgeDataEvent {

    // задача выполнена участником сети
    data class SubTaskCompleted(
        val taskId: Int,
        val result: EdgeResult
    ) : EdgeDataEvent

    // новая задача от участника сети
    data class NewRemoteTask(
        val task: EdgeSubTaskBasic
    ) : EdgeDataEvent

    data class PeersChanged(
        val peers: List<EdgeDevice>
    ) : EdgeDataEvent

    data class Error(
        val cause: Throwable
    ) : EdgeDataEvent
}