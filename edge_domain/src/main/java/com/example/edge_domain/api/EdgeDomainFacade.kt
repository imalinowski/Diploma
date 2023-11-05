package com.example.edge_domain.api

import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_domain.internal.EdgeControllerImpl

object EdgeDomainFacade {

    fun provideEdgeController(
        edgeDomainDependencies: EdgeDomainDependencies
    ): EdgeController {
        return EdgeControllerImpl(edgeDomainDependencies)
    }

}