package com.example.edge_domain.api.dependecies.data

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import kotlinx.coroutines.flow.SharedFlow

interface EdgeData {
    val eventsFromDataFlow: SharedFlow<EdgeDataEvent> // поток обратой связи с Domain

    suspend fun exitFromNetwork()
    suspend fun getOnlineDevices(): List<EdgeDevice>

    suspend fun executeTaskByDevice(
        deviceName: String,
        task: EdgeSubTaskBasic
    )

    suspend fun sendRemoteTaskResult(
        task: EdgeSubTaskBasic
    )

}