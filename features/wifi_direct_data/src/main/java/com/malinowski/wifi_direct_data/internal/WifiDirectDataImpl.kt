package com.malinowski.wifi_direct_data.internal

import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WifiDirectDataImpl
@Inject constructor(
    private val repository: WifiDirectDataRepository
) : EdgeData {

    override val eventsFromDataFlow: Flow<EdgeDataEvent>
        get() = repository.eventsFlow

    override suspend fun exitFromNetwork() {
        repository.exit()
    }

    override suspend fun getOnlineDevices(): List<EdgeDevice> {
        return repository.getOnlineDevices()
    }

    override suspend fun executeTaskByDevice(device: EdgeDevice, task: EdgeSubTaskBasic) {
        repository.executeByDevice(
            device = device,
            task = task
        )
    }

    override suspend fun sendToRemoteTaskResult(task: EdgeSubTaskBasic) {
        repository.sendToRemoteTaskResult(
            result = task.getEndResult()
        )
    }
}