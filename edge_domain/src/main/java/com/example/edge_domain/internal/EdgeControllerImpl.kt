package com.example.edge_domain.internal

import com.example.edge_domain.api.EdgeController
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies

internal class EdgeControllerImpl(
    edgeDomainDependencies: EdgeDomainDependencies
) : EdgeController {

    val edgeUi = edgeDomainDependencies.edgeUi
    val edgeData = edgeDomainDependencies.edgeData

}