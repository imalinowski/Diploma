package com.example.edge_domain.internal.executor

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.edge_entities.tasks.EdgeTaskBasic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

internal interface EdgeTaskExecutor : CoroutineScope {

    val completedTaskFlow: Flow<EdgeTaskExecutorEvent> // поток обратой связи с Domain

    fun executeTask(task: EdgeTaskBasic, devices: List<EdgeDevice>)

    fun executeRemoteTask(task: EdgeSubTaskBasic)

    fun completeSubTask(taskId: Int, result: EdgeResult)

}