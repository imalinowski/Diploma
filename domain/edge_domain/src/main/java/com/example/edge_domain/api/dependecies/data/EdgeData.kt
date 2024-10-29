package com.example.edge_domain.api.dependecies.data

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import kotlinx.coroutines.flow.Flow

/*
*                       Схема архитектуры
*
* --> асинхронная подписка
* <== прямой вызов ( поток управления )
*
* |      | --> |      | --> |        | --> |      | --> |      |
* |  WD  |     | DATA |     | Domain |     |  UI  |     |  VM  |
* |      | <== |      | <== |        | <== |      | <== |      |
*
* WD - WI-FI direct
* VM - ViewModel
*
*/

interface EdgeData {

    // приходят события из data слоя ( wifi direct / network )
    // уходят в domain
    val eventsFromData: Flow<EdgeDataEvent>

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