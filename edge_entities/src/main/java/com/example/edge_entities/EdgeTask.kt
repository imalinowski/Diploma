package com.example.edge_entities

import com.example.edge_entities.EdgeTask.MatrixMultiply.MatrixMultiplyParams
import com.example.edge_entities.EdgeTask.MatrixMultiply.MatrixMultiplyResult
import com.example.edge_entities.EdgeTask.Params
import com.example.edge_entities.EdgeTask.Result

const val MATRIX_MULTIPLY_NAME = "MatrixMultiply"

sealed interface EdgeTask<P : Params, R : Result> {

    val name: String

    interface Params

    interface Result

    fun execute(params: P): R

    object MatrixMultiply : EdgeTask<MatrixMultiplyParams, MatrixMultiplyResult> {

        override val name: String = MATRIX_MULTIPLY_NAME

        data class MatrixMultiplyParams(
            val matrixA: List<List<Int>>,
            val matrixB: List<List<Int>>,
        ) : Params

        data class MatrixMultiplyResult(
            val matrix: List<List<Int>>
        ) : Result

        override fun execute(params: MatrixMultiplyParams): MatrixMultiplyResult {
            TODO("Not yet implemented")
        }

    }

}