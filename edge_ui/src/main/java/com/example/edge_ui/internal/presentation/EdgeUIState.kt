package com.example.edge_ui.internal.presentation

import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult
import java.lang.Integer.min

private const val MATRIX_STUB = "x x x x x x\nx x x x x x\nx x x x x x\nx x x x x x\n"
private const val LINE_STUB = "... ... ... ... ... ... ..."
private const val MATRIX_SIZE_UI_LIMIT = 5

internal data class EdgeUIState(
    val params: MatrixMultiplyParams? = null,
    val result: MatrixMultiplyResult? = null
) {

    val uiMatrixA get() = squeezeMatrix(params?.matrixA)
    val uiMatrixB get() = squeezeMatrix(params?.matrixB)
    val uiMatrixResult get() = squeezeMatrix(result?.matrix)

    private fun squeezeMatrix(matrix: List<List<Int>>?): String {
        var squeezeMatrix = ""

        if (!matrix.isNullOrEmpty()) {
            for (i in 0 until min(MATRIX_SIZE_UI_LIMIT, matrix.size)) {
                val matrixLine = when {
                    i < MATRIX_SIZE_UI_LIMIT / 2 -> matrix[i]
                    i > MATRIX_SIZE_UI_LIMIT / 2 -> matrix[matrix.size - i]
                    else -> null
                }
                squeezeMatrix += squeezeLine(matrixLine)
            }
        }

        return squeezeMatrix.ifEmpty { MATRIX_STUB }
    }

    private fun squeezeLine(line: List<Int>?): String {
        if (line == null) {
            return LINE_STUB + "\n"
        }

        val start = line.subList(0, MATRIX_SIZE_UI_LIMIT)
            .joinToString(separator = ",")
        val end = line.subList(line.size - MATRIX_SIZE_UI_LIMIT, line.size)
            .joinToString(separator = ",")

        val startStr = start.substring(0, MATRIX_SIZE_UI_LIMIT)
        val endStr = end.substring(end.length - MATRIX_SIZE_UI_LIMIT)

        return "$startStr ... $endStr\n"
    }

}