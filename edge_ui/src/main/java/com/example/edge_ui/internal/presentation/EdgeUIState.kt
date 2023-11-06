package com.example.edge_ui.internal.presentation

import com.example.edge_entities.EdgeParams
import com.example.edge_entities.EdgeResult

internal data class EdgeUIState(
    val params: EdgeParams? = null,
    val result: EdgeResult? = null
)