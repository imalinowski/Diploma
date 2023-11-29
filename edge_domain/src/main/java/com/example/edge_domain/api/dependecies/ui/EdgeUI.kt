package com.example.edge_domain.api.dependecies.ui

import com.example.edge_entities.EdgeResult
import kotlinx.coroutines.flow.Flow

interface EdgeUI {
    val eventsFromUIFlow: Flow<EdgeUiEvent> // поток обратой связи с UI

    suspend fun showInfo(text: String)
    suspend fun showResult(result: EdgeResult)
    suspend fun taskInProgress(info: String)
}