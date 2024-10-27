package com.example.edge_domain.internal.executor

import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.RemoteTaskCompleted
import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.SendTaskToRemote
import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.TaskCompleted
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.edge_entities.tasks.EdgeTaskBasic
import com.example.edge_entities.tasks.TaskStatus.READY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

// TODO move implementation to entities module

internal class EdgeTaskExecutorImpl : EdgeTaskExecutor {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private val _completedTaskFlow = MutableSharedFlow<EdgeTaskExecutorEvent>()
    override val completedTaskFlow = _completedTaskFlow.asSharedFlow()

    private var localTask: EdgeTaskBasic? = null
    private val localDevice = EdgeDevice("local") // todo сделать через Dagger грамотную заглушку

    private val remoteTasks: MutableList<EdgeSubTaskBasic> = mutableListOf()

    override fun executeTask(task: EdgeTaskBasic, devices: List<EdgeDevice>) {
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

    override fun executeRemoteTask(task: EdgeSubTaskBasic) {
        remoteTasks.add(task)
        execute()
    }

    override fun completeSubTask(taskId: Int, result: EdgeResult) {
        localTask?.apply {
            completeSubTask(taskId, result)
            if (getCurrentStatus() == READY) {
                newEvent(
                    TaskCompleted(this)
                )
                localTask = null
            }
        }
    }

    private fun execute() {
        launch {
            while (hasTasks()) {
                val task = getTask().apply {
                    execute()
                }
                newEvent(
                    RemoteTaskCompleted(task)
                )
            }
        }
    }

    private fun newEvent(event: EdgeTaskExecutorEvent) {
        launch {
            _completedTaskFlow.emit(event)
        }
    }

    private fun hasTasks(): Boolean {
        return remoteTasks.isNotEmpty()
    }

    private fun getTask(): EdgeSubTaskBasic {
        val task = synchronized(remoteTasks) {
            remoteTasks.removeAt(0)
        }
        return task
    }

}