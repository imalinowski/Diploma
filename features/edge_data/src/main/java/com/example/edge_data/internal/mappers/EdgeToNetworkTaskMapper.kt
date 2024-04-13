package com.example.edge_data.internal.mappers

import com.example.edge_data.internal.models.NetworkTask
import com.example.edge_data.internal.models.NetworkTaskContent
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class EdgeToNetworkTaskMapper {

    fun map(task: EdgeSubTaskBasic, deviceName: String): NetworkTask {
        val content = NetworkTaskContent(
            taskName = task.name,
            parentId = task.parentId,
            deviceName = deviceName,
            params = Json.encodeToString(task.params)
        )
        return NetworkTask(
            id = task.id,
            content = Json.encodeToString(content)
        )
    }

}