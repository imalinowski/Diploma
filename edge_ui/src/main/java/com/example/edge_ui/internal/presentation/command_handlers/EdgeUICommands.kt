package com.example.edge_ui.internal.presentation.command_handlers

import com.example.edge_entities.EdgeParams.MatrixMultiplyParams

internal sealed interface EdgeUICommands {

    class AddMatrixTask(
        val params: MatrixMultiplyParams
    ) : EdgeUICommands

    sealed class GenerateMatrix(
        val size: Int? = null
    ) : EdgeUICommands {

        class MatrixA(size: Int? = null) : GenerateMatrix(size)

        class MatrixB(size: Int? = null) : GenerateMatrix(size)
    }

}