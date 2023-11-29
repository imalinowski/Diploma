package com.example.edge_data.internal.mappers

import com.example.edge_data.internal.models.NetworkTask
import com.example.edge_data.internal.models.NetworkTaskContent
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTask
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.edge_entities.tasks.MatrixMultiplySubTask
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
internal class NetworkToEdgeTaskMapper {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun map(task: NetworkTask): EdgeSubTaskBasic {
        val content: NetworkTaskContent = Json.decodeFromString(task.content)
        val task = MatrixMultiplySubTask(
            id = task.id,
            parentId = content.parentId,
            firstLineIndex = -1,
            params = json.decodeFromString(content.params)
        )
        return task as EdgeSubTask<EdgeResult>
    }
}