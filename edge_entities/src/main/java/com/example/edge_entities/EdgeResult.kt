package com.example.edge_entities

sealed interface EdgeResult {

    data class MatrixMultiplyResult(
        val matrix: List<List<Int>>
    ) : EdgeResult
}