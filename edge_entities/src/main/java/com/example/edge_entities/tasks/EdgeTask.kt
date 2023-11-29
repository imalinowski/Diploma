package com.example.edge_entities.tasks

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult

typealias EdgeTaskBasic = EdgeTask<EdgeSubTask<EdgeResult>, EdgeResult>
typealias EdgeSubTaskBasic = EdgeSubTask<EdgeResult>

sealed interface EdgeTask<EdgeSubTask, Result> {
    val id: Int
    val name: String

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

sealed interface EdgeSubTask<Result> {
    val id: Int
    val name: String
    val parentId: Int

    fun execute(): Result

    fun completeTask(result: Result)

    fun getEndResult(): Result

    fun getInfo(): String
}

