package com.example.edge_entities.tasks

import com.example.edge_entities.EdgeParams
import com.example.edge_entities.EdgeResult

class MatrixMultiply(
    private val params: EdgeParams.MatrixMultiplyParams
) : EdgeTask {

    override val name: String = MATRIX_MULTIPLY_NAME

    override fun execute(): EdgeResult.MatrixMultiplyResult {
        val matrixA = params.matrixA
        val matrixB = params.matrixB

        val matrix = MutableList(params.matrixA.size) {
            MutableList(params.matrixB.size) { 0 }
        }

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                for (k in matrixA[i].indices) {
                    matrix[i][j] += matrixA[i][k] * matrixB[k][j]
                }
            }
        }

        return EdgeResult.MatrixMultiplyResult(
            matrix = matrix
        )
    }

}