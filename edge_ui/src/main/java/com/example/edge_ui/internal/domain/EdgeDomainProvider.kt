package com.example.edge_ui.internal.domain

import com.example.edge_data.api.EdgeDataDependencies
import com.example.edge_data.api.EdgeDataFacade
import com.example.edge_domain.api.EdgeDomain
import com.example.edge_domain.api.EdgeDomainFacade
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_ui.api.EdgeUIFacade

//todo move to dagger subcomponnent
internal fun provideEdgeDomainController(): EdgeDomain {
    val edgeDomainDependencies = object : EdgeDomainDependencies {
        override val edgeUi = EdgeUIFacade.provideEdgeUI()
        override val edgeData = EdgeDataFacade.provideEdgeData(
            object : EdgeDataDependencies {} // todo provide real dependecies
        )
    }
    return EdgeDomainFacade.provideEdgeController(edgeDomainDependencies)
}