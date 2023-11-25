package com.example.edge_ui.internal.presentation

import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult
import java.lang.Integer.min

private const val MATRIX_STUB = "x x x x x x\nx x x x x x\nx x x x x x\nx x x x x x\n"
private const val LINE_STUB = "..."

private const val MATRIX_SIZE_UI_LIMIT = 5
private const val LINE_LENGTH_LIMIT = 10

internal data class EdgeUIState(
    val matrixSize: Int = MATRIX_SIZE_UI_LIMIT,
    val params: MatrixMultiplyParams? = null,
    val result: MatrixMultiplyResult? = null,
) {

    val uiMatrixA get() = squeezeMatrix(params?.matrixA)
    val uiMatrixB get() = squeezeMatrix(params?.matrixB)
    val uiMatrixResult get() = squeezeMatrix(result?.matrix)

    private fun squeezeMatrix(matrix: List<List<Int>>?): String {
        var squeezeMatrix = ""

        if (!matrix.isNullOrEmpty()) {
            for (i in 0 until getSizeLimit(matrix.size)) {
                val matrixLine = when {
                    matrix.size <= MATRIX_SIZE_UI_LIMIT -> matrix[i]
                    i < MATRIX_SIZE_UI_LIMIT / 2 -> matrix[i]
                    i > MATRIX_SIZE_UI_LIMIT / 2 -> matrix[matrix.size - i]
                    else -> null
                }
                squeezeMatrix += squeezeLine(matrixLine)
            }
        }

        return squeezeMatrix.ifEmpty { MATRIX_STUB }
    }

    private fun getSizeLimit(matrixSize: Int): Int {
        return min(MATRIX_SIZE_UI_LIMIT, matrixSize)
    }

    private fun squeezeLine(line: List<Int>?): String {
        if (line == null) {
            return LINE_STUB + "\n"
        }
        val subLine = line.subList(0, getSizeLimit(line.size))
            .joinToString(" ")
        return "${subLine.squeeze()}\n"
    }

    private fun String.squeeze(): String {
        return if (length > LINE_LENGTH_LIMIT) {
            "${substring(0, LINE_LENGTH_LIMIT)}..."
        } else this
    }

}