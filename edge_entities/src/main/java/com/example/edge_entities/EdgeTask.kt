package com.example.edge_entities

const val MATRIX_MULTIPLY_NAME = "MatrixMultiply"

sealed interface EdgeTask {

    val name: String

    interface Params

    interface Result

    fun execute(): Result

    class MatrixMultiply(
        private val params: MatrixMultiplyParams? = null
    ) : EdgeTask {

        override val name: String = MATRIX_MULTIPLY_NAME

        data class MatrixMultiplyParams(
            val matrixA: List<List<Int>>,
            val matrixB: List<List<Int>>,
        ) : Params

        data class MatrixMultiplyResult(
            val matrix: List<List<Int>>
        ) : Result

        override fun execute(): MatrixMultiplyResult {
            return MatrixMultiplyResult(
                matrix = listOf()
            )
        }

    }

}