package com.malinowski.wifi_direct_data.internal

import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.NewRemoteTask
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.SubTaskCompleted
import com.example.edge_entities.EdgeParams
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTask
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.edge_entities.tasks.MatrixMultiplySubTask
import com.example.wifi_direct.api.WifiDirectEvents.MessageData
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WifiDirectEventsMapper
@Inject constructor() : (MessageData) -> EdgeDataEvent {

    override fun invoke(event: MessageData): EdgeDataEvent {
        val (author, messageSerialized) = event.message
        val message = Json.decodeFromString<WifiDirectTaskMessage>(messageSerialized)
        return when (message.type) {
            WifiDirectTaskMessageType.Task -> getTaskFromRemote(
                author = author ?: "",
                message = message
            )
            WifiDirectTaskMessageType.Result -> getResultFromRemote(message.content)
        }
    }

    private fun getTaskFromRemote(author: String, message: WifiDirectTaskMessage): NewRemoteTask {
        val params = Json.decodeFromString<EdgeParams>(message.content)
        return NewRemoteTask(
            author = author,
            task = getTaskByParams(message.taskId, params)
        )
    }
    @Suppress("UNCHECKED_CAST")
    private fun getTaskByParams(taskId: Int, params: EdgeParams): EdgeSubTaskBasic {
        return when (params) {
            is EdgeParams.MatrixMultiplyParams ->
                MatrixMultiplySubTask(
                    id = taskId,
                    params = params,
                    firstLineIndex = -1,
                    parentId = 0
                ) as EdgeSubTask<EdgeResult>
        }
    }

    private fun getResultFromRemote(content: String): SubTaskCompleted {
        val result = Json.decodeFromString<EdgeResult>(content)
        return SubTaskCompleted(
            taskId = result.taskId, // todo remove taskId
            result = result
        )
    }
}