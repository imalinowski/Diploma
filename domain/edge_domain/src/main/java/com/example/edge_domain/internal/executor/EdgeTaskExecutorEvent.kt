package com.example.edge_domain.internal.executor

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.edge_entities.tasks.EdgeTaskBasic

sealed interface EdgeTaskExecutorEvent {

    class TaskCompleted(
        val task: EdgeTaskBasic
    ) : EdgeTaskExecutorEvent

    class RemoteTaskCompleted(
        val task: EdgeSubTaskBasic
    ) : EdgeTaskExecutorEvent

    class SendTaskToRemote(
        val task: EdgeSubTaskBasic,
        val device: EdgeDevice
    ) : EdgeTaskExecutorEvent

}