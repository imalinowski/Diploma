package com.example.edge_domain.api.dependecies.data

import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic

sealed interface EdgeDataEvent {

    // taks sended by host is completed
    class SubTaskCompleted(
        val taskId: Int,
        val result: EdgeResult
    ) : EdgeDataEvent

    //host sended a task
    data class NewRemoteTask(
        val task: EdgeSubTaskBasic
    ) : EdgeDataEvent

}