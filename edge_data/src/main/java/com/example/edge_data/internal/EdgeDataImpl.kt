package com.example.edge_data.internal

import com.example.edge_data.api.EdgeDataDependencies
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeTaskBasic
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

    override fun executeTaskByDevice(deviceName: String, task: EdgeTaskBasic) {

    }

    override fun sendTaskResult(result: EdgeResult) {

    }
}