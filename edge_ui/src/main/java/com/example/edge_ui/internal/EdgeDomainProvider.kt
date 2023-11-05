package com.example.edge_ui.internal

import com.example.edge_data.api.EdgeDataFacade
import com.example.edge_domain.api.EdgeController
import com.example.edge_domain.api.EdgeDomainFacade
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_ui.api.EdgeUIFacade

internal fun provideEdgeDomainController(): EdgeController {
    val edgeDomainDependencies = object : EdgeDomainDependencies {
        override val edgeUi = EdgeUIFacade.provideEdgeUI()
        override val edgeData = EdgeDataFacade.provideEdgeData()
    }
    return EdgeDomainFacade.provideEdgeController(edgeDomainDependencies)
}