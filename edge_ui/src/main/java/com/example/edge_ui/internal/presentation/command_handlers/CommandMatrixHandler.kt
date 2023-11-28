package com.example.edge_ui.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerated
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix.GenerateMatrixA
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix.GenerateMatrixB
import kotlin.math.min
import kotlin.random.Random

internal const val MATRIX_SIZE_LIMIT = 2000
private const val MATRIX_ELEMENT_LIMIT = 1000

internal class CommandMatrixHandler : CommandHandler<EdgeUICommands, EdgeUIEvents> {

    override suspend fun handle(command: EdgeUICommands): EdgeUIEvents? {
        if (command !is GenerateMatrix) {
            return null
        }
        val size = getMatrixSize(command.size)
        val matrix = List(size) {
            List(size) { Random.nextInt(0, MATRIX_ELEMENT_LIMIT) }
        }
        return when (command) {
            is GenerateMatrixA -> MatrixGenerated.GeneratedMatrixA(matrix = matrix)
            is GenerateMatrixB -> MatrixGenerated.GeneratedMatrixB(matrix = matrix)
        }
    }

    private fun getMatrixSize(userSize: Int?): Int {
        return min(MATRIX_SIZE_LIMIT, userSize ?: MATRIX_SIZE_LIMIT)
    }
}