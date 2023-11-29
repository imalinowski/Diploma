package com.example.edge_entities

import kotlinx.serialization.Serializable

@Serializable
sealed interface EdgeResult {

    @Serializable
    data class MatrixMultiplyResult(
        val matrix: List<List<Int>>
    ) : EdgeResult
}