package com.example.edge_domain.api.dependecies.data

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import kotlinx.coroutines.flow.Flow

interface EdgeData {
    val eventsFromDataFlow: Flow<EdgeDataEvent> // поток обратой связи с Domain

    suspend fun getOnlineDevices(): List<EdgeDevice>

    suspend fun executeTaskByDevice(
        deviceName: String,
        task: EdgeSubTaskBasic
    )

    suspend fun sendRemoteTaskResult(
        task: EdgeSubTaskBasic
    )

}