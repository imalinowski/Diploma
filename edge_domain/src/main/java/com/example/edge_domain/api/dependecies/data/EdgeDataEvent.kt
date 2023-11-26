package com.example.edge_domain.api.dependecies.data

import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeTask

sealed interface EdgeDataEvent {

    // taks sended by host is completed
    class TaskCompleted(
        val id: Int,
        val result: EdgeResult
    ) : EdgeDataEvent

    //host sended a task
    data class NewTask(
        val task: EdgeTask
    ) : EdgeDataEvent

}