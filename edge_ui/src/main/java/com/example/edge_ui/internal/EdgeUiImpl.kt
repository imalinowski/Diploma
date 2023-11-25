package com.example.edge_ui.internal

import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_domain.api.dependecies.ui.EdgeUiEvent
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatricesMultiplied
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

internal class EdgeUiImpl(
    private val flowToUI: MutableSharedFlow<EdgeUIEvents> // через этот поток UI слушает domain
) : EdgeUI {

    override val eventsToUIFlow: Flow<EdgeUiEvent> = flow { } // через этот поток domain слушает UI
    override suspend fun showResult(result: EdgeResult) {
        when (result) {
            is MatrixMultiplyResult -> {
                flowToUI.emit(MatricesMultiplied(result))
            }
            else -> Unit
        }
    }

}