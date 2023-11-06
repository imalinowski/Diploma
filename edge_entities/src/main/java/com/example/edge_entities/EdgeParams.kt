package com.example.edge_entities

sealed interface EdgeParams {

    data class MatrixMultiplyParams(
        val matrixA: List<List<Int>> = listOf(),
        val matrixB: List<List<Int>> = listOf(),
    ) : EdgeParams

}