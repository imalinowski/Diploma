package com.example.edge_entities

import kotlinx.serialization.Serializable

@Serializable
sealed interface EdgeResult {

    data class MatrixMultiplyResult(
        val matrix: List<List<Int>>
    ) : EdgeResult
}