package com.example.edge_domain.internal

import com.example.edge_domain.api.EdgeController
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_entities.tasks.EdgeTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class EdgeControllerImpl(
    edgeDomainDependencies: EdgeDomainDependencies
) : EdgeController, CoroutineScope {
    override val coroutineContext: CoroutineContext //maybe launch in service
        get() = Job() + Dispatchers.Default

    private val edgeUi: EdgeUI = edgeDomainDependencies.edgeUi
    private val edgeData: EdgeData = edgeDomainDependencies.edgeData

    private val taskList: MutableList<EdgeTask> = mutableListOf()

    init {
        launch {
            edgeData.eventsFlow.collect {} // поток сообщений от data слоя
        }
    }

    override fun addTask(task: EdgeTask) {
        taskList.add(task)
        executeTask()
    }

    private fun executeTask() {
        val task = taskList.removeAt(0)
        val result = task.execute()
        launch {
            edgeUi.showResult(result)
        }
    }

}