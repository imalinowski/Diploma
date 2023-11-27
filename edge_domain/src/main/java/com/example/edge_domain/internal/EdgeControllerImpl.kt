package com.example.edge_domain.internal

import com.example.edge_domain.api.EdgeController
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.NewTask
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.TaskCompleted
import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_entities.tasks.EdgeTaskBasic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class EdgeControllerImpl(
    edgeDomainDependencies: EdgeDomainDependencies
) : EdgeController, CoroutineScope {
    override val coroutineContext: CoroutineContext //maybe launch in service
        get() = Job() + Dispatchers.IO

    private val edgeUi: EdgeUI = edgeDomainDependencies.edgeUi
    private val edgeData: EdgeData = edgeDomainDependencies.edgeData

    private val taskList: MutableList<EdgeTaskBasic> = mutableListOf()

    init {
        launch {
            edgeData.eventsFromDataFlow.collect(::processDataEvents)
        }
    }

    override fun addTask(task: EdgeTaskBasic) {
        taskList.add(task)
        launch(Dispatchers.IO) {
            executeTask()
        }
    }

    private suspend fun executeTask() {
        val task = taskList.removeAt(0)
        edgeUi.taskInProgress(task.getInfo())

        val subTasks = task.parallel(10)
        subTasks.forEach { subTask ->
            val result = subTask.execute()
            task.completeSubTask(subTask.id, result)
        }
        val result = task.getEndResult()
        delay(10000)

        edgeUi.showResult(result)
    }

    private fun processDataEvents(event: EdgeDataEvent) {
        when (event) {
            is NewTask -> taskList.add(event.task)
            is TaskCompleted -> {} //subTaskCompleted(event)
        }
    }

    private fun subTaskCompleted(event: TaskCompleted) {
        val task = taskList.find {
            it.id == event.id
        } ?: return
        taskList.remove(task)
    }

}