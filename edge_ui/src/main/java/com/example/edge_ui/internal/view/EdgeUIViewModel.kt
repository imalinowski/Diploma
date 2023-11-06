package com.example.edge_ui.internal.view

import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.AddNewMatrixTask
import com.example.edge_ui.internal.presentation.EdgeUIEvents.GenerateMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.GenerateMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerated
import com.example.edge_ui.internal.presentation.EdgeUIState
import com.example.edge_ui.internal.presentation.command_handlers.CommandCoreHandler
import com.example.edge_ui.internal.presentation.command_handlers.CommandMatrixHandler
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix

internal class EdgeUIViewModel : Store<EdgeUIState, EdgeUICommands, EdgeUIEvents>(
    initialState = EdgeUIState(),
    commandHandlers = listOf(
        CommandMatrixHandler(),
        CommandCoreHandler()
    )
) {
    override fun dispatch(event: EdgeUIEvents) {
        when (event) {
            is AddNewMatrixTask -> command {
                AddMatrixTask(event.params)
            }

            GenerateMatrixA -> command {
                GenerateMatrix.MatrixA()
            }

            GenerateMatrixB -> command {
                GenerateMatrix.MatrixB()
            }

            is MatrixGenerated -> newState { setMatrices(event) }
        }

    }

    private fun EdgeUIState.setMatrices(event: MatrixGenerated): EdgeUIState {

        var newParams = (params as? MatrixMultiplyParams) ?: MatrixMultiplyParams()

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

}