package com.malinowski.wifi_direct_data.internal

import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.NewRemoteTask
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.SubTaskCompleted
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.wifi_direct.api.WifiDirectEvents.MessageData
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WifiDirectEventsMapper
@Inject constructor() : (MessageData) -> EdgeDataEvent {

    override fun invoke(event: MessageData): EdgeDataEvent {
        val (author, messageSerialized) = event.message
        val message = Json.decodeFromString<WifiDirectTaskMessage>(messageSerialized)
        return when (message.type) {
            WifiDirectTaskMessageType.Task -> getTaskFromRemote(author ?: "", message.content)
            WifiDirectTaskMessageType.Result -> getResultFromRemote(message.content)
        }
    }

    private fun getTaskFromRemote(author: String, content: String): NewRemoteTask {
        val task = Json.decodeFromString<EdgeSubTaskBasic>(content)
        return NewRemoteTask(
            author = author,
            task = task
        )
    }

    private fun getResultFromRemote(content: String): SubTaskCompleted {
        val result = Json.decodeFromString<EdgeResult>(content)
        return SubTaskCompleted(
            taskId = result.taskId, // todo remove taskId
            result = result
        )
    }
}