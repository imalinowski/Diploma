package com.example.edge_data.internal

import com.example.edge_data.api.EdgeDataDependencies
import com.example.edge_data.internal.mappers.EdgeToNetworkTaskMapper
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.entities.EdgeDevice
import com.example.entities.tasks.EdgeSubTaskBasic
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class EdgeDataImpl(
    val dependencies: EdgeDataDependencies, // почему не используется ??
    private val repository: EdgeDataRepository,
    private val taskMapper: EdgeToNetworkTaskMapper,
) : EdgeData {

    override val eventsFromData: SharedFlow<EdgeDataEvent>
        get() = repository.eventsFlow.asSharedFlow()

    override suspend fun exitFromNetwork() {
        repository.exit()
    }

    override suspend fun getOnlineDevices(): List<EdgeDevice> {
        return repository.getOnlineDevices()
    }

    override suspend fun executeTaskByDevice(device: EdgeDevice, task: EdgeSubTaskBasic) {
        repository.executeByDevice(
            deviceName = device.name,
            task = taskMapper.map(task, device.name)
        )
    }

    override suspend fun sendToRemoteTaskResult(task: EdgeSubTaskBasic) {
        val result = Json.encodeToString(task.getEndResult())
        repository.sendToRemoteTaskResult(
            taskId = task.id, result = result
        )
    }
}