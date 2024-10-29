package com.example.edge_ui.internal.domain

import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatricesMultiplied
import com.example.edge_ui.internal.presentation.EdgeUIEvents.RemoteTaskCompleted
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowInfo
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowLocalTaskInProgress
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowRemoteTaskInProgress
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EdgeUiImpl
@Inject constructor() : EdgeUI {

    override val eventsFromDomain = MutableSharedFlow<EdgeUIEvents>() // через этот поток domain слушает UI

    override suspend fun showInfo(text: String) {
        eventsFromDomain.emit(
            ShowInfo(text)
        )
    }

    override suspend fun showResult(result: EdgeResult) {
        when (result) {
            is MatrixMultiplyResult -> {
                eventsFromDomain.emit(MatricesMultiplied(result))
            }

            else -> Unit
        }
    }

    override suspend fun localTaskInProgress(info: String) {
        eventsFromDomain.emit(
            ShowLocalTaskInProgress(info)
        )
    }

    override suspend fun remoteTaskInProgress(info: String) {
        eventsFromDomain.emit(
            ShowRemoteTaskInProgress(info)
        )
    }

    override suspend fun remoteTaskCompleted() {
        eventsFromDomain.emit(
            RemoteTaskCompleted
        )
    }

}