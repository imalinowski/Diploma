package com.example.edge_data.api

import com.example.edge_data.internal.EdgeDataImpl
import com.example.edge_data.internal.EdgeDataRepository
import com.example.edge_data.internal.mappers.EdgeToNetworkTaskMapper
import com.example.edge_data.internal.mappers.NetworkToEdgeTaskMapper
import com.example.edge_domain.api.dependecies.data.EdgeData

object EdgeDataFacade {

    private val edgeToNetworkTaskMapper = EdgeToNetworkTaskMapper()
    private val networkToEdgeTaskMapper = NetworkToEdgeTaskMapper()

    fun provideEdgeData(
        dependencies: EdgeDataDependencies
    ): EdgeData {
        val edgeDataRepository = EdgeDataRepository(
            deviceName = dependencies.deviceName,
            mapper = networkToEdgeTaskMapper
        )
        return EdgeDataImpl(
            dependencies,
            edgeDataRepository,
            edgeToNetworkTaskMapper
        )
    }
}