package com.example.edge_domain.internal.executor

import com.example.entities.EdgeDevice
import com.example.entities.tasks.EdgeResult
import com.example.entities.tasks.EdgeSubTaskBasic
import com.example.entities.tasks.EdgeTaskBasic
import kotlinx.coroutines.flow.Flow

internal interface EdgeTaskExecutor {

    val completedTaskFlow: Flow<EdgeTaskExecutorEvent> // поток обратой связи с Domain

    suspend fun executeTask(task: EdgeTaskBasic, devices: List<EdgeDevice>)

    suspend fun completeSubTask(taskId: Int, result: EdgeResult)

    suspend fun executeRemoteTask(task: EdgeSubTaskBasic)
}