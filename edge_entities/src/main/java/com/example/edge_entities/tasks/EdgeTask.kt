package com.example.edge_entities.tasks

sealed interface EdgeTask<EdgeSubTask, Result> {
    val id: Int
    val name: String

    fun parallel(devices: Int): List<EdgeSubTask>

    fun completeSubTask(id: Int, result: Result)

    fun getCurrentStatus(): TaskStatus
}

enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    READY
}

sealed interface EdgeSubTask<Result> {
    val parentId: Int

    fun execute(): Result

    fun completeTask(result: Result)
}

