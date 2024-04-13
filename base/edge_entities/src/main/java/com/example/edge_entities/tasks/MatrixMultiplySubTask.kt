package com.example.edge_entities.tasks

import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult

private typealias EdgeSubTaskMatrixMultiply = EdgeSubTask<MatrixMultiplyResult>

class MatrixMultiplySubTask(
    params: MatrixMultiplyParams,
    val firstLineIndex: Int, // part of parent's matrixA lines from started
    override val id: Int,
    override val parentId: Int,
) : MatrixMultiply(id, params), EdgeSubTaskMatrixMultiply {

    override fun execute(): MatrixMultiplyResult {
        status = TaskStatus.IN_PROGRESS
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

        status = TaskStatus.READY
        return MatrixMultiplyResult(
            matrix = matrix
        ).also {
            result = it
        }
    }

    override fun completeTask(result: MatrixMultiplyResult) {
        this.result = result
        status = TaskStatus.READY
    }

}