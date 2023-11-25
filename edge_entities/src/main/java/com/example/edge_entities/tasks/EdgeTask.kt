package com.example.edge_entities.tasks

import com.example.edge_entities.EdgeResult

const val MATRIX_MULTIPLY_NAME = "MatrixMultiply"

sealed interface EdgeTask {

    val name: String

    fun execute(): EdgeResult

}