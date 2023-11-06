package com.example.edge_entities

import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult

const val MATRIX_MULTIPLY_NAME = "MatrixMultiply"

sealed interface EdgeTask {

    val name: String

    fun execute(): EdgeResult

    class MatrixMultiply(
        private val params: MatrixMultiplyParams
    ) : EdgeTask {

        override val name: String = MATRIX_MULTIPLY_NAME

        override fun execute(): MatrixMultiplyResult {
            return MatrixMultiplyResult(
                matrix = listOf()
            )
        }

    }

}