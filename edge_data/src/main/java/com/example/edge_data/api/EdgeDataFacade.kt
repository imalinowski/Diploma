package com.example.edge_data.api

import com.example.edge_data.internal.EdgeDataImpl
import com.example.multitask_domain.api.dependecies.data.EdgeData

object EdgeDataFacade {

    fun provideEdgeData(): EdgeData {
        return EdgeDataImpl()
    }

}