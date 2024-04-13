package com.example.edge_domain.api

import com.example.edge_entities.tasks.EdgeTaskBasic

// Ручка общения с Контроллером для слоев более низкого порядка
interface EdgeDomain {
    fun addTaskFromUI(task: EdgeTaskBasic)

    fun exitFromNetwork()
}