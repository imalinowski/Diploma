package com.example.multitaskfeature.internal

import androidx.lifecycle.ViewModel
import com.example.edge_data.api.EdgeDataFacade
import com.example.multitask_domain.api.EdgeController
import com.example.multitask_domain.api.EdgeDomainFacade
import com.example.multitask_domain.api.dependecies.EdgeDomainDependencies
import com.example.multitaskfeature.api.EdgeUIFacade

internal class EdgeUIViewModel : ViewModel() {

    private val edgeUi: EdgeController

    init {
        val edgeDomainDependencies = object : EdgeDomainDependencies {
            override val edgeUi = EdgeUIFacade.provideEdgeUI()
            override val edgeData = EdgeDataFacade.provideEdgeData()
        }
        edgeUi = EdgeDomainFacade.provideEdgeController(edgeDomainDependencies)
    }

}