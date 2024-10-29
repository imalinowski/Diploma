package com.example.edge_domain.internal.executor

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.edge_entities.tasks.EdgeTaskBasic
import kotlinx.coroutines.flow.Flow

interface EdgeTaskExecutor {

    val completedTaskFlow: Flow<EdgeTaskExecutorEvent> // поток обратой связи с Domain

    suspend fun executeTask(task: EdgeTaskBasic, devices: List<EdgeDevice>)

    suspend fun completeSubTask(taskId: Int, result: EdgeResult)

    suspend fun executeRemoteTask(task: EdgeSubTaskBasic)
}