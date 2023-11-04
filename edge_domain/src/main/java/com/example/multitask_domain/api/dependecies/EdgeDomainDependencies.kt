package com.example.multitask_domain.api.dependecies

import com.example.multitask_domain.api.dependecies.data.EdgeData
import com.example.multitask_domain.api.dependecies.ui.EdgeUI

interface EdgeDomainDependencies {
    val edgeUi: EdgeUI
    val edgeData: EdgeData
}