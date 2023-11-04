package com.example.multitask_domain.api

import com.example.multitask_domain.api.dependecies.EdgeDomainDependencies
import com.example.multitask_domain.internal.EdgeControllerImpl

object EdgeDomainFacade {

    fun provideEdgeController(
        edgeDomainDependencies: EdgeDomainDependencies
    ): EdgeController {
        return EdgeControllerImpl(edgeDomainDependencies)
    }

}