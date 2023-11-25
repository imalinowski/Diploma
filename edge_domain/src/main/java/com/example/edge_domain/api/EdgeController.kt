package com.example.edge_domain.api

import com.example.edge_entities.tasks.EdgeTask

// Ручка общения с Контроллером для слоев более низкого порядка
interface EdgeController {
    fun addTask(task: EdgeTask)
}