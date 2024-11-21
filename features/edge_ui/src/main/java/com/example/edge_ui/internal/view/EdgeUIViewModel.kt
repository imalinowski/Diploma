package com.example.edge_ui.internal.view

import androidx.lifecycle.viewModelScope
import com.example.common_arch.Store
import com.example.edge_ui.internal.domain.EdgeUiImpl
import com.example.edge_ui.internal.presentation.EdgeUIEffects
import com.example.edge_ui.internal.presentation.EdgeUIEffects.ShowAlertView
import com.example.edge_ui.internal.presentation.EdgeUIEffects.ShowToast
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate.ClickGenerateMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate.ClickGenerateMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.AddNewMatrixTask
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.MatricesMultiplied
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.ShowLocalTaskInProgress
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.ShowRemoteTaskInProgress
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.UpdatePeersCounter
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerated
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerated.GeneratedMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixGenerated.GeneratedMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixSizeChanged
import com.example.edge_ui.internal.presentation.EdgeUIEvents.PeersCounterClicked
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowErrorAlert
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowInfo
import com.example.edge_ui.internal.presentation.command_handlers.CommandDomainHandler
import com.example.edge_ui.internal.presentation.command_handlers.CommandMatrixHandler
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.EnterNetwork
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.ExitFromNetwork
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix.GenerateMatrixA
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix.GenerateMatrixB
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.RequestUpdatePeersCounter
import com.example.edge_ui.internal.presentation.command_handlers.MATRIX_SIZE_LIMIT
import com.example.edge_ui.internal.view.model.EdgeUiTaskInfoState
import com.example.entities.tasks.EdgeParams.MatrixMultiplyParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.edge_ui.internal.presentation.EdgeUIEffects as Effects
import com.example.edge_ui.internal.presentation.EdgeUIEvents as Events
import com.example.edge_ui.internal.presentation.EdgeUIState as State
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands as Commands

private const val UI_INFO_GENERATING_MATRIX = "Generating Matrix"
private const val SIZE_LIMIT_TOAST = "Matrix Size is Limited by $MATRIX_SIZE_LIMIT"

internal class EdgeUIViewModel
@Inject constructor(
    commandDomainHandler: CommandDomainHandler,
    commandMatrixHandler: CommandMatrixHandler,
    edgeUI: EdgeUiImpl,
) : Store<State, Commands, Events, Effects>(
    initialState = State(),
    commandHandlers = listOf(
        commandMatrixHandler, commandDomainHandler
    )
) {

    override val storeScope: CoroutineScope = viewModelScope
    private val edgeUiEventsFromDomain = edgeUI.eventsFromDomain

    init {
        commands {
            val size = state.matrixSize
            listOf(
                EnterNetwork,
                GenerateMatrixA(size),
                GenerateMatrixB(size),
            )
        }
        viewModelScope.launch {
            edgeUiEventsFromDomain.collect(::dispatch)
        }
    }

    override fun onCleared() {
        command { ExitFromNetwork }
    }

    override fun dispatch(event: Events) {
        when (event) {
            is ShowInfo -> newEffect {
                ShowToast(event.info)
            }

            is ShowErrorAlert -> newEffect {
                ShowAlertView(event.title, event.info)
            }

            PeersCounterClicked -> {
                newState { state.copy(peersCounter = "Loading...") }
                command { RequestUpdatePeersCounter }
            }

            is MatrixSizeChanged -> parseSizeFromUi(event.matrixSize)

            is ClickedGenerate -> generateMatrix(event)

            is MatrixGenerated -> newState {
                setMatrices(event).copy(localTaskInfo = null)
            }

            is DomainEvents -> handleDomainEvents(state, event)
        }
    }

    private fun handleDomainEvents(state: State, event: DomainEvents) {
        when (event) {
            is UpdatePeersCounter -> newState {
                copy(peersCounter = event.peers.toString())
            }

            is MatricesMultiplied -> newState {
                copy(result = event.result, localTaskInfo = null)
            }

            is ShowLocalTaskInProgress -> newState {
                copy(localTaskInfo = getTaskInfoState(event.info))
            }

            is ShowRemoteTaskInProgress -> newState {
                copy(remoteTaskInfo = getTaskInfoState(event.info))
            }

            DomainEvents.RemoteTaskCompleted -> newState {
                copy(remoteTaskInfo = null)
            }

            is AddNewMatrixTask -> if (state.params != null) {
                command { AddMatrixTask(state.params) }
            }
        }
    }

    private fun parseSizeFromUi(sizeFromUI: CharSequence?) {
        val size = sizeFromUI.toString().toIntOrNull()
        if ((size ?: 0) > MATRIX_SIZE_LIMIT) {
            newEffect {
                ShowToast(SIZE_LIMIT_TOAST)
            }
            return
        }
        newState {
            copy(
                matrixSize = size ?: matrixSize,
                localTaskInfo = getTaskInfoState(UI_INFO_GENERATING_MATRIX),
                params = null
            )
        }
        command { GenerateMatrixA(size = state.matrixSize) }
        command { GenerateMatrixB(size = state.matrixSize) }
    }

    private fun generateMatrix(event: ClickedGenerate) {
        newState {
            copy(localTaskInfo = getTaskInfoState(UI_INFO_GENERATING_MATRIX))
        }
        when (event) {
            ClickGenerateMatrixA -> command { GenerateMatrixA(size = state.matrixSize) }
            ClickGenerateMatrixB -> command { GenerateMatrixB(size = state.matrixSize) }
        }
    }

    private fun State.setMatrices(event: MatrixGenerated): State {
        var newParams = params ?: MatrixMultiplyParams()

        newParams = when (event) {
            is GeneratedMatrixA -> {
                newParams.copy(matrixA = event.matrix)
            }

            is GeneratedMatrixB -> {
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