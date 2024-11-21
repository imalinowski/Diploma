package com.example.edge_ui.internal.domain

import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.entities.tasks.EdgeResult
import com.example.entities.tasks.EdgeResult.MatrixMultiplyResult
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.MatricesMultiplied
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.RemoteTaskCompleted
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.ShowLocalTaskInProgress
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.ShowRemoteTaskInProgress
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.UpdatePeersCounter
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EdgeUiImpl
@Inject constructor() : EdgeUI {

    override val eventsFromDomain = MutableSharedFlow<EdgeUIEvents>()

    override suspend fun updatePeersCounter(peers: Int) {
        eventsFromDomain.emit(
            UpdatePeersCounter(peers)
        )
    }

    override suspend fun showInfo(text: String) {
        eventsFromDomain.emit(
            ShowInfo(text)
        )
    }

    override suspend fun showError(title: String, text: String) {
        eventsFromDomain.emit(
            EdgeUIEvents.ShowErrorAlert(title, text)
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