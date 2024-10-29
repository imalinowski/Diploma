package com.example.edge_domain.internal

import com.example.edge_domain.api.EdgeDomain
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.Error
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.NewRemoteTask
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.SubTaskCompleted
import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_domain.internal.executor.EdgeTaskExecutor
import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent
import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.RemoteTaskCompleted
import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.SendTaskToRemote
import com.example.edge_domain.internal.executor.EdgeTaskExecutorEvent.TaskCompleted
import com.example.edge_entities.tasks.EdgeTaskBasic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


internal class EdgeDomainImpl(
    dependencies: EdgeDomainDependencies,
    private val taskExecutor: EdgeTaskExecutor,
) : EdgeDomain, CoroutineScope {
    override val coroutineContext: CoroutineContext // TODO maybe launch in service
        get() = Job() + Dispatchers.IO

    private val edgeUi: EdgeUI = dependencies.edgeUi
    private val edgeData: EdgeData = dependencies.edgeData

    init {
        launch {
            edgeData.eventsFromDataFlow.collect(::dispatchDataEvents)
        }
        launch {
            taskExecutor.completedTaskFlow.collect(::dispatchExecutorEvents)
        }
    }

    override fun addTaskFromUI(task: EdgeTaskBasic) {
        launch {
            val devices = edgeData.getOnlineDevices()
            if (devices.isEmpty()) {
                edgeUi.showInfo("No peers in network")
            }
            edgeUi.localTaskInProgress(task.getInfo())
            taskExecutor.executeTask(task, devices)
        }
    }

    override fun exitFromNetwork() {
        launch {
            edgeData.exitFromNetwork()
        }
    }

    private fun dispatchDataEvents(event: EdgeDataEvent) {
        when (event) {
            is NewRemoteTask -> launch {
                edgeUi.showInfo("New task from Remote!")
                edgeUi.remoteTaskInProgress("Computing  task from Remote \n ${event.task.getInfo()}")
                taskExecutor.executeRemoteTask(event.task)
            }

            is SubTaskCompleted -> launch {
                edgeUi.showInfo("Network has completed task! \n ${event.taskId}")
                taskExecutor.completeSubTask(event.taskId, event.result)
            }

            is Error -> launch {
                edgeUi.showInfo("Error On Data Layer ${event.cause.javaClass.name}")
            }
        }
    }

    private fun dispatchExecutorEvents(event: EdgeTaskExecutorEvent) {
        when (event) {
            is TaskCompleted -> launch {
                edgeUi.showResult(event.task.getEndResult())
            }

            is SendTaskToRemote -> launch {
                try {
                    edgeData.executeTaskByDevice(event.device, event.task)
                } catch (e: Throwable) {
                    edgeUi.showInfo("Error \n" + e.message)
                }
            }

            is RemoteTaskCompleted -> launch {
                edgeUi.remoteTaskCompleted()
                try {
                    edgeData.sendToRemoteTaskResult(event.task)
                } catch (e: Throwable) {
                    edgeUi.showInfo("Error \n" + e.message)
                }
            }
        }
    }
}