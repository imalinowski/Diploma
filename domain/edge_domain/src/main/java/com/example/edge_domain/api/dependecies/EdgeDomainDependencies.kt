package com.example.edge_domain.api.dependecies

import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.ui.EdgeUI

// todo use dagger
interface EdgeDomainDependencies {
    val edgeUi: EdgeUI
    val edgeData: EdgeData
}