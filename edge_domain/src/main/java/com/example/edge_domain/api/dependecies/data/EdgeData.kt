package com.example.edge_domain.api.dependecies.data

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeTask
import kotlinx.coroutines.flow.Flow

interface EdgeData {
    val eventsFromDataFlow: Flow<EdgeDataEvent> // поток обратой связи с Domain

    suspend fun getOnlineDevices(): List<EdgeDevice>

    fun executeTaskByDevice(
        deviceName: String,
        task: EdgeTask
    )

    fun sendTaskResult(
        result: EdgeResult
    )

}