package com.example.edge_ui.internal.presentation

import com.example.edge_domain.api.dependecies.ui.EdgeUiEvent
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult

sealed interface EdgeUIEvents: EdgeUiEvent {

    data class ShowInfo(val info: String) : EdgeUIEvents

    data class MatrixSizeChanged(
        val matrixSize: CharSequence?
    ) : EdgeUIEvents

    data object PeersCounterClicked: EdgeUIEvents

    sealed interface ClickedGenerate : EdgeUIEvents {
        data object ClickGenerateMatrixA : ClickedGenerate
        data object ClickGenerateMatrixB : ClickedGenerate
    }

    sealed interface DomainEvents : EdgeUIEvents {
        data class UpdatePeersCounter(val peers: Int) : DomainEvents
        data class ShowLocalTaskInProgress(val info: String) : DomainEvents
        data class ShowRemoteTaskInProgress(val info: String) : DomainEvents
        data object RemoteTaskCompleted : DomainEvents
        data object AddNewMatrixTask : DomainEvents
        data class MatricesMultiplied(
            val result: MatrixMultiplyResult
        ) : DomainEvents
    }

    sealed interface MatrixGenerated : EdgeUIEvents {

        data class GeneratedMatrixA(
            val matrix: List<List<Int>>
        ) : MatrixGenerated

        data class GeneratedMatrixB(
            val matrix: List<List<Int>>
        ) : MatrixGenerated
    }
}