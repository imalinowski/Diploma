package com.example.edge_domain.api.dependecies.ui

import com.example.edge_entities.EdgeResult
import kotlinx.coroutines.flow.Flow

interface EdgeUI {
    val eventsToUIFlow: Flow<EdgeUiEvent> // поток обратой связи с UI

    suspend fun showResult(result: EdgeResult)
}