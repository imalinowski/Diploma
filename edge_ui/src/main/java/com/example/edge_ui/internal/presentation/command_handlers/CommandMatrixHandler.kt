package com.example.edge_ui.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerated
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix.MatrixA
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix.MatrixB
import kotlin.random.Random

private const val MATRIX_SIZE_LIMIT = 1000
private const val MATRIX_ELEMENT_LIMIT = 1000

internal class CommandMatrixHandler : CommandHandler<EdgeUICommands, EdgeUIEvents> {

    override fun handle(command: EdgeUICommands): EdgeUIEvents? {
        if (command !is GenerateMatrix) {
            return null
        }
        val size = command.size ?: Random.nextInt(MATRIX_SIZE_LIMIT)
        val matrix = List(size) {
            List(size) { Random.nextInt(0,MATRIX_ELEMENT_LIMIT) }
        }
        return when (command) {
            is MatrixA -> MatrixGenerated.MatrixA(matrix = matrix)
            is MatrixB -> MatrixGenerated.MatrixB(matrix = matrix)
        }
    }

}