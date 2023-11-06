package com.example.edge_ui.internal.presentation

import com.example.edge_entities.EdgeParams

internal sealed interface EdgeUIEvents {
    class AddNewMatrixTask(
        val params: EdgeParams.MatrixMultiplyParams
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