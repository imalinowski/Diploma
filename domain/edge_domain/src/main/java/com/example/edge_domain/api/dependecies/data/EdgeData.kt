package com.example.edge_domain.api.dependecies.data

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface EdgeData {

    // в этот поток приходят события из data слоя
    // Нужно подписаться чтобы ловить входящие задачи
    val eventsFromDataFlow: Flow<EdgeDataEvent>

    suspend fun exitFromNetwork()
    suspend fun getOnlineDevices(): List<EdgeDevice>

    suspend fun executeTaskByDevice(
        device: EdgeDevice,
        task: EdgeSubTaskBasic
    )
    // как будто можно делегировать распределение по устройствам на data слой
    // чтобы domain не знал контретики об устройствах сети

    suspend fun sendToRemoteTaskResult(
        task: EdgeSubTaskBasic
    )
}