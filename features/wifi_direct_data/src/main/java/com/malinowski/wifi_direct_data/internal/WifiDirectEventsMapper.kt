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
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessage
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessageType.Result
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessageType.Task
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WifiDirectEventsMapper
@Inject constructor() : (MessageData) -> EdgeDataEvent? {

    override fun invoke(event: MessageData): EdgeDataEvent? {
        try {
            val message = Json.decodeFromString<WifiDirectTaskMessage>(event.message.text)
            return when (val type = message.type) {
                is Task -> getTaskFromRemote(
                    taskId = type.taskId, content = message.content
                )

                Result -> getResultFromRemote(
                    content = message.content
                )

                else -> null
            }
        } catch (e: Throwable) {
            // todo figure out why message wrong
            return null
        }
    }

    private fun getTaskFromRemote(taskId: Int, content: String): NewRemoteTask {
        val params = Json.decodeFromString<EdgeParams>(content)
        return NewRemoteTask(
            task = getTaskByParams(taskId, params)
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