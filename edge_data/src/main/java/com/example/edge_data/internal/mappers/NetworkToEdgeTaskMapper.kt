package com.example.edge_data.internal.mappers

import com.example.edge_data.internal.models.NetworkTask
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTask
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.edge_entities.tasks.MatrixMultiplySubTask
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
internal class NetworkToEdgeTaskMapper {

    fun map(task: NetworkTask): EdgeSubTaskBasic {
        return MatrixMultiplySubTask(
            id = task.id,
            parentId = task.content.parentId,
            firstLineIndex = -1,
            params = Json.decodeFromString(task.content.params)
        ) as EdgeSubTask<EdgeResult>
    }
}