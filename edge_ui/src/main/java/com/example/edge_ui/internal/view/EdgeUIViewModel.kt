package com.example.edge_ui.internal.view

import androidx.lifecycle.ViewModel
import com.example.edge_ui.internal.provideEdgeDomainController

internal class EdgeUIViewModel : ViewModel() {

    val domainController = provideEdgeDomainController()

}