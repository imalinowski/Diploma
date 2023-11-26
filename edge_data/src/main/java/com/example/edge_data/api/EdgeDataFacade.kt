package com.example.edge_data.api

import com.example.edge_data.internal.EdgeDataImpl
import com.example.edge_data.internal.EdgeDataRepository
import com.example.edge_domain.api.dependecies.data.EdgeData

object EdgeDataFacade {

    private val edgeDataRepository = EdgeDataRepository()

    fun provideEdgeData(
        dependencies: EdgeDataDependencies
    ): EdgeData {
        return EdgeDataImpl(dependencies, edgeDataRepository)
    }

}