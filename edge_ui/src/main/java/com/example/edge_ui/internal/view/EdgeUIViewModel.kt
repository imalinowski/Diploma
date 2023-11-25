package com.example.edge_ui.internal.view

import androidx.lifecycle.viewModelScope
import com.example.common_arch.Store
import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_ui.api.EdgeUIFacade
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.AddNewMatrixTask
import com.example.edge_ui.internal.presentation.EdgeUIEvents.GenerateMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.GenerateMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatricesMultiplied
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerated
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixSizeChanged
import com.example.edge_ui.internal.presentation.EdgeUIState
import com.example.edge_ui.internal.presentation.command_handlers.CommandCoreHandler
import com.example.edge_ui.internal.presentation.command_handlers.CommandMatrixHandler
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix
import kotlinx.coroutines.launch

internal class EdgeUIViewModel : Store<EdgeUIState, EdgeUICommands, EdgeUIEvents>(
    initialState = EdgeUIState(),
    commandHandlers = listOf(
        CommandMatrixHandler(),
        CommandCoreHandler()
    )
) {

    private val edgeUiEventsFromDomain = EdgeUIFacade.provideEventsToUIFlow()

    init {
        viewModelScope.launch {
            edgeUiEventsFromDomain.collect(::dispatch)
        }
    }

    override fun dispatch(event: EdgeUIEvents) {
        val state = state.value
        when (event) {

            is MatrixSizeChanged -> parseSizeFromUi(event.matrixSize)

            GenerateMatrixA -> command {
                GenerateMatrix.MatrixA(size = state.matrixSize)
            }

            GenerateMatrixB -> command {
                GenerateMatrix.MatrixB(size = state.matrixSize)
            }

            is AddNewMatrixTask -> if (state.params != null) {
                command { AddMatrixTask(state.params) }
            }

            is MatrixGenerated -> newState { setMatrices(event) }

            is MatricesMultiplied -> newState {
                copy(result = event.result)
            }
        }

    }

    private fun EdgeUIState.setMatrices(event: MatrixGenerated): EdgeUIState {
        var newParams = params ?: MatrixMultiplyParams()

        newParams = when (event) {
            is MatrixGenerated.MatrixA -> {
                newParams.copy(matrixA = event.matrix)
            }

            is MatrixGenerated.MatrixB -> {
                newParams.copy(matrixB = event.matrix)
            }
        }

        return copy(params = newParams)
    }

    private fun parseSizeFromUi(sizeFromUI: CharSequence?) {
        val size = sizeFromUI.toString().toIntOrNull()
        newState {
            copy(matrixSize = size ?: matrixSize)
        }

        val state = state.value
        command { GenerateMatrix.MatrixA(size = state.matrixSize) }
        command { GenerateMatrix.MatrixB(size = state.matrixSize) }
    }

}