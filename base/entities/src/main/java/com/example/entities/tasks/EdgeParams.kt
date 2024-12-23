package com.example.entities.tasks

import kotlinx.serialization.Serializable

@Serializable
sealed interface EdgeParams {

    fun getId() = this.hashCode()

    @Serializable
    data class MatrixMultiplyParams(
        val matrixA: List<List<Int>> = listOf(), // todo use arrays ?
        val matrixB: List<List<Int>> = listOf(),
    ) : EdgeParams

}