package com.example.edge_ui.internal.presentation.command_handlers

import com.example.edge_entities.EdgeParams.MatrixMultiplyParams

internal sealed interface EdgeUICommands {

    class AddMatrixTask(
        val params: MatrixMultiplyParams
    ) : EdgeUICommands

    sealed class GenerateMatrix(
        val size: Int? = null
    ) : EdgeUICommands {

        class GenerateMatrixA(size: Int? = null) : GenerateMatrix(size)

        class GenerateMatrixB(size: Int? = null) : GenerateMatrix(size)
    }

}