package com.example.edge_ui.internal.domain

import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_domain.api.dependecies.ui.EdgeUiEvent
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatricesMultiplied
import com.example.edge_ui.internal.presentation.EdgeUIEvents.RemoteTaskCompleted
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowInfo
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowLocalTaskInProgress
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowRemoteTaskInProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

internal class EdgeUiImpl(
    private val flowToUI: MutableSharedFlow<EdgeUIEvents> // через этот поток UI слушает domain
) : EdgeUI {

    override val eventsFromUIFlow: Flow<EdgeUiEvent> = flow { } // через этот поток domain слушает UI
    override suspend fun showInfo(text: String) {
        flowToUI.emit(
            ShowInfo(text)
        )
    }

    override suspend fun showResult(result: EdgeResult) {
        when (result) {
            is MatrixMultiplyResult -> {
                flowToUI.emit(MatricesMultiplied(result))
            }

            else -> Unit
        }
    }

    override suspend fun localTaskInProgress(info: String) {
        flowToUI.emit(
            ShowLocalTaskInProgress(info)
        )
    }

    override suspend fun remoteTaskInProgress(info: String) {
        flowToUI.emit(
            ShowRemoteTaskInProgress(info)
        )
    }

    override suspend fun remoteTaskCompleted() {
        flowToUI.emit(
            RemoteTaskCompleted
        )
    }

}