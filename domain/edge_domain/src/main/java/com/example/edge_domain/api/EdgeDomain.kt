package com.example.edge_domain.api

import com.example.entities.tasks.EdgeTaskBasic

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

// Ручка общения с Контроллером для слоев более низкого порядка
interface EdgeDomain {

    suspend fun addTaskFromUI(task: EdgeTaskBasic)

    suspend fun exitFromNetwork()

    suspend fun updatePeersCounter(): Int
}