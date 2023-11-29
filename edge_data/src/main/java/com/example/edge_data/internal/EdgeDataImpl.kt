package com.example.edge_data.internal

import com.example.edge_data.api.EdgeDataDependencies
import com.example.edge_data.internal.mappers.EdgeToNetworkTaskMapper
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class EdgeDataImpl(
    val dependencies: EdgeDataDependencies,
    private val repository: EdgeDataRepository,
    private val taskMapper: EdgeToNetworkTaskMapper,
) : EdgeData {

    override val eventsFromDataFlow: Flow<EdgeDataEvent> = repository.eventsFlow.asSharedFlow()

    override suspend fun getOnlineDevices(): List<EdgeDevice> {
        return repository.getOnlineDevices()
    }

    override suspend fun executeTaskByDevice(deviceName: String, task: EdgeSubTaskBasic) {
        repository.executeByDevice(
            deviceName = deviceName,
            task = taskMapper.map(task, deviceName)
        )
    }

    override suspend fun sendRemoteTaskResult(task: EdgeSubTaskBasic) {
        val result = Json.encodeToString(task.getEndResult())
        repository.sendToRemoteTaskResult(
            taskId = task.id, result = result
        )
    }
}