package com.example.edge_data.internal

import com.example.edge_data.api.EdgeDataDependencies
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class EdgeDataImpl(
    val dependencies: EdgeDataDependencies,
    private val edgeDataRepository: EdgeDataRepository
) : EdgeData {

    override val eventsFromDataFlow: Flow<EdgeDataEvent> = flow { }
    override suspend fun getOnlineDevices(): List<EdgeDevice> {
        return edgeDataRepository.getOnlineDevices()
    }

    override fun executeTaskByDevice(deviceName: String, task: EdgeSubTaskBasic) {
        TODO("Not yet implemented")
    }

    override fun sendRemoteTaskResult(task: EdgeSubTaskBasic) {
        TODO("Not yet implemented")
    }
}