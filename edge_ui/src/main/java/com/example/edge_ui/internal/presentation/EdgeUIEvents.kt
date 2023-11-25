package com.example.edge_ui.internal.presentation

import com.example.edge_entities.EdgeResult.MatrixMultiplyResult

internal sealed interface EdgeUIEvents {

    class MatrixSizeChanged(
        val matrixSize: CharSequence?
    ) : EdgeUIEvents

    object AddNewMatrixTask : EdgeUIEvents

    class MatricesMultiplied(
        val result: MatrixMultiplyResult
    ) : EdgeUIEvents

    object GenerateMatrixA : EdgeUIEvents

    object GenerateMatrixB : EdgeUIEvents

    sealed interface MatrixGenerated : EdgeUIEvents {

        class MatrixA(
            val matrix: List<List<Int>>
        ) : MatrixGenerated

        class MatrixB(
            val matrix: List<List<Int>>
        ) : MatrixGenerated
    }
}