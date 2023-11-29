package com.example.edge_ui.internal.presentation

import com.example.edge_entities.EdgeResult.MatrixMultiplyResult

internal sealed interface EdgeUIEvents {

    class ShowInfo(val info: String) : EdgeUIEvents

    class ShowLocalTaskInProgress(val info: String) : EdgeUIEvents
    class ShowRemoteTaskInProgress(val info: String) : EdgeUIEvents
    data object RemoteTaskCompleted : EdgeUIEvents

    class MatrixSizeChanged(
        val matrixSize: CharSequence?
    ) : EdgeUIEvents

    data object AddNewMatrixTask : EdgeUIEvents

    class MatricesMultiplied(
        val result: MatrixMultiplyResult
    ) : EdgeUIEvents

    sealed interface ClickedGenerate : EdgeUIEvents {
        data object ClickGenerateMatrixA : ClickedGenerate
        data object ClickGenerateMatrixB : ClickedGenerate
    }

    sealed interface MatrixGenerated : EdgeUIEvents {

        class GeneratedMatrixA(
            val matrix: List<List<Int>>
        ) : MatrixGenerated

        class GeneratedMatrixB(
            val matrix: List<List<Int>>
        ) : MatrixGenerated
    }
}