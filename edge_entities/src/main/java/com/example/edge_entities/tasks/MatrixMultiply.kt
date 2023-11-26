package com.example.edge_entities.tasks

import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult

open class MatrixMultiply(
    override val id: Int,
    private val params: MatrixMultiplyParams
) : EdgeTask {

    override val name: String = MATRIX_MULTIPLY_NAME
    override val subTasks: List<EdgeSubTask> = listOf()

    override fun parallel(devices: Int): List<EdgeSubTask> {
        return subTasks
    }

    override fun completeSubTask(id: Int, result: EdgeResult) {
        TODO("Not yet implemented")
    }

    override fun getCurrentStatus(): TaskStatus {
        TODO("Not yet implemented")
    }

}

class MatrixMultiplySubTask(
    id: Int,
    private val params: MatrixMultiplyParams,
    override val parentId: Int,
) : MatrixMultiply(id, params), EdgeSubTask {

    override fun execute(): MatrixMultiplyResult {
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

        return MatrixMultiplyResult(matrix = matrix)
    }

}