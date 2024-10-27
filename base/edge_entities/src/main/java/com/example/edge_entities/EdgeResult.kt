package com.example.edge_entities

import kotlinx.serialization.Serializable

@Serializable
sealed interface EdgeResult {

    val taskId: Int

    @Serializable
    data class MatrixMultiplyResult(
        override val taskId: Int,
        val matrix: List<List<Int>>
    ) : EdgeResult
}