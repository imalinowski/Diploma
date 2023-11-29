package com.example.edge_entities

import kotlinx.serialization.Serializable
@Serializable
sealed interface EdgeParams {

    fun getId() = this.hashCode()
    @Serializable
    data class MatrixMultiplyParams(
        val matrixA: List<List<Int>> = listOf(),
        val matrixB: List<List<Int>> = listOf(),
    ) : EdgeParams

}