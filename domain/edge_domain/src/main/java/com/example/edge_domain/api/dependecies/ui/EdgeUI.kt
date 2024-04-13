package com.example.edge_domain.api.dependecies.ui

import com.example.edge_entities.EdgeResult
import kotlinx.coroutines.flow.Flow

interface EdgeUI {
    val eventsFromUIFlow: Flow<EdgeUiEvent> // поток обратой связи с UI

    suspend fun showInfo(text: String)
    suspend fun showResult(result: EdgeResult)
    suspend fun localTaskInProgress(info: String)
    suspend fun remoteTaskInProgress(info: String)
    suspend fun remoteTaskCompleted()
}