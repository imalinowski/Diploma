package com.example.edge_entities.tasks

import com.example.edge_entities.EdgeResult

const val MATRIX_MULTIPLY_NAME = "MatrixMultiply"

sealed interface EdgeTask {
    val id: Int
    val name: String
    val subTasks: List<EdgeSubTask>

    fun parallel(devices: Int): List<EdgeSubTask>

    fun completeSubTask(id: Int, result: EdgeResult)

    fun getCurrentStatus(): TaskStatus
}

enum class TaskStatus {
    IN_PROGRESS,
    READY
}

sealed interface EdgeSubTask : EdgeTask {
    val parentId: Int

    fun execute(): EdgeResult
}

