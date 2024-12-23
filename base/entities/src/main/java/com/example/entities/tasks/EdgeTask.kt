package com.example.entities.tasks

import com.example.entities.EdgeDevice

typealias EdgeTaskBasic = EdgeTask<EdgeSubTask<EdgeResult>, EdgeResult>
typealias EdgeSubTaskBasic = EdgeSubTask<EdgeResult>

sealed interface EdgeTask<EdgeSubTask, Result> {
    val id: Int
    val name: String
    val params: EdgeParams

    fun parallel(devices: List<EdgeDevice>): Map<EdgeDevice, EdgeSubTask>

    fun completeSubTask(id: Int, result: Result)

    fun getCurrentStatus(): TaskStatus

    fun getEndResult(): Result

    fun getInfo(): String
}

enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    READY
}

// todo remove subtask
// идея - любая подзадача тоже является задачей, поэтому смысла в сущности SubTask нет

sealed interface EdgeSubTask<Result> {
    val id: Int
    val name: String
    val parentId: Int // todo remove ?
    val params: EdgeParams

    fun execute(): Result

    fun completeTask(result: Result)

    fun getEndResult(): Result

    fun getInfo(): String
}

