package com.example.edge_entities.tasks

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeParams
import com.example.edge_entities.EdgeResult

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
    val parentId: Int
    val params: EdgeParams

    fun execute(): Result

    fun completeTask(result: Result)

    fun getEndResult(): Result

    fun getInfo(): String
}

