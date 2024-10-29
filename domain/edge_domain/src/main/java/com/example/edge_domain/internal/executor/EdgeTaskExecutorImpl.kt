package com.example.edge_domain.internal.executor

import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.RemoteTaskCompleted
import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.SendTaskToRemote
import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.TaskCompleted
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.edge_entities.tasks.EdgeTaskBasic
import com.example.edge_entities.tasks.TaskStatus.READY
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

// TODO move implementation to entities module
class EdgeTaskExecutorImpl
@Inject constructor() : EdgeTaskExecutor {

    private val _completedTaskFlow = MutableSharedFlow<EdgeTaskExecutorEvent>()
    override val completedTaskFlow = _completedTaskFlow.asSharedFlow()

    private var localTask: EdgeTaskBasic? = null
    private val localDevice = EdgeDevice("local") // todo сделать через Dagger грамотную заглушку

    override suspend fun executeTask(task: EdgeTaskBasic, devices: List<EdgeDevice>) {
        localTask = task
        val subTasks = task.parallel(devices + localDevice)
        for ((device, subTask) in subTasks) {
            if (device == localDevice) {
                val result = subTask.execute()
                completeSubTask(result.taskId, result)
            } else {
                newEvent(SendTaskToRemote(subTask, device))
            }
        }
    }

    override suspend fun completeSubTask(taskId: Int, result: EdgeResult) {
        localTask?.apply {
            completeSubTask(taskId, result)
            if (getCurrentStatus() == READY) {
                newEvent(TaskCompleted(this))
                localTask = null
            }
        }
    }

    override suspend fun executeRemoteTask(task: EdgeSubTaskBasic) {
        task.execute()
        newEvent(RemoteTaskCompleted(task))
    }

    private suspend fun newEvent(event: EdgeTaskExecutorEvent) {
        _completedTaskFlow.emit(event)
    }
}