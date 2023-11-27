package com.example.edge_ui.internal.view

import androidx.lifecycle.viewModelScope
import com.example.common_arch.Store
import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_ui.api.EdgeUIFacade
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.AddNewMatrixTask
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatricesMultiplied
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerate
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerate.GenerateMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerate.GenerateMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerated
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixSizeChanged
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowTaskInProgress
import com.example.edge_ui.internal.presentation.EdgeUIState
import com.example.edge_ui.internal.presentation.command_handlers.CommandCoreHandler
import com.example.edge_ui.internal.presentation.command_handlers.CommandMatrixHandler
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix.MatrixA
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix.MatrixB
import com.example.edge_ui.internal.view.model.EdgeUiTaskInfoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val UI_INFO_GENERATING_MATRIX = "Generating Matrix"

internal class EdgeUIViewModel : Store<EdgeUIState, EdgeUICommands, EdgeUIEvents>(
    initialState = EdgeUIState(),
    commandHandlers = listOf(
        CommandMatrixHandler(),
        CommandCoreHandler()
    )
) {

    override val storeScope: CoroutineScope = viewModelScope
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

            is MatrixGenerate -> generateMatrix(event)

            is AddNewMatrixTask -> if (state.params != null) {
                command { AddMatrixTask(state.params) }
            }

            is MatrixGenerated -> newState {
                setMatrices(event).copy(taskInfo = null)
            }

            is MatricesMultiplied -> newState {
                copy(result = event.result, taskInfo = null)
            }

            is ShowTaskInProgress -> newState {
                copy(taskInfo = getTaskInfoState(event.info))
            }
        }

    }

    private fun parseSizeFromUi(sizeFromUI: CharSequence?) {
        val size = sizeFromUI.toString().toIntOrNull()
        newState {
            copy(
                matrixSize = size ?: matrixSize,
                taskInfo = getTaskInfoState(UI_INFO_GENERATING_MATRIX)
            )
        }

        val state = state.value
        command { MatrixA(size = state.matrixSize) }
        command { MatrixB(size = state.matrixSize) }
    }

    private fun generateMatrix(event: MatrixGenerate) {
        val state = state.value
        newState {
            copy(taskInfo = getTaskInfoState(UI_INFO_GENERATING_MATRIX))
        }
        when (event) {
            GenerateMatrixA -> command { MatrixA(size = state.matrixSize) }

            GenerateMatrixB -> command { MatrixB(size = state.matrixSize) }
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

    private fun getTaskInfoState(info: String): EdgeUiTaskInfoState {
        return EdgeUiTaskInfoState(
            info = info,
            showProgress = true
        )
    }
}