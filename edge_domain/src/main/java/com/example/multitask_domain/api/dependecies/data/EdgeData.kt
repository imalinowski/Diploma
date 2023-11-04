package com.example.multitask_domain.api.dependecies.data

import kotlinx.coroutines.flow.Flow

interface EdgeData {
    val eventsFlow: Flow<EdgeDataEvent> // поток обратой связи с Data
}