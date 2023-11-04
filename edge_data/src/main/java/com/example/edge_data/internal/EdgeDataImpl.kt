package com.example.edge_data.internal

import com.example.multitask_domain.api.dependecies.data.EdgeData
import com.example.multitask_domain.api.dependecies.data.EdgeDataEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class EdgeDataImpl : EdgeData {
    override val eventsFlow: Flow<EdgeDataEvent> = flow { }
}