package com.example.edge_data.internal

import com.example.edge_data.api.EdgeDataDependencies
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class EdgeDataImpl(
    val dependencies: EdgeDataDependencies
) : EdgeData {
    override val eventsFlow: Flow<EdgeDataEvent> = flow { }
}