package com.example.edge_domain.internal.executor

import com.example.entities.EdgeDevice
import com.example.entities.tasks.EdgeSubTaskBasic
import com.example.entities.tasks.EdgeTaskBasic

internal sealed interface EdgeTaskExecutorEvent {

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