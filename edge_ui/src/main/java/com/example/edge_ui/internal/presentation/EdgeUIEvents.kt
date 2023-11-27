package com.example.edge_ui.internal.presentation

import com.example.edge_entities.EdgeResult.MatrixMultiplyResult

internal sealed interface EdgeUIEvents {

    class ShowTaskInProgress(val info: String) : EdgeUIEvents

    class MatrixSizeChanged(
        val matrixSize: CharSequence?
    ) : EdgeUIEvents

    data object AddNewMatrixTask : EdgeUIEvents

    class MatricesMultiplied(
        val result: MatrixMultiplyResult
    ) : EdgeUIEvents

    sealed interface MatrixGenerate : EdgeUIEvents {
        data object GenerateMatrixA : MatrixGenerate
        data object GenerateMatrixB : MatrixGenerate
    }

    sealed interface MatrixGenerated : EdgeUIEvents {

        class MatrixA(
            val matrix: List<List<Int>>
        ) : MatrixGenerated

        class MatrixB(
            val matrix: List<List<Int>>
        ) : MatrixGenerated
    }
}