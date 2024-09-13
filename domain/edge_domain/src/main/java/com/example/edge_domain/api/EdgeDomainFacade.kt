package com.example.edge_domain.api

import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_domain.internal.EdgeDomainImpl
import com.example.edge_domain.internal.executor.EdgeTaskExecutorImpl

object EdgeDomainFacade {

    private val edgeTaskExecutor = EdgeTaskExecutorImpl()

    // use dagger
    fun provideEdgeController(
        edgeDomainDependencies: EdgeDomainDependencies
    ): EdgeDomain {
        return EdgeDomainImpl(
            edgeDomainDependencies,
            edgeTaskExecutor
        )
    }
}