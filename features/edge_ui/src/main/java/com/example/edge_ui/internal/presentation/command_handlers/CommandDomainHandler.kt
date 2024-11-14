package com.example.edge_ui.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.edge_domain.api.EdgeDomain
import com.example.edge_entities.tasks.EdgeTaskBasic
import com.example.edge_entities.tasks.MatrixMultiply
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.UpdatePeersCounter
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ShowInfo
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.ExitFromNetwork
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.GenerateMatrix
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.RequestUpdatePeersCounter
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
internal class CommandDomainHandler
@Inject constructor(
    private val edgeDomain: EdgeDomain
) : CommandHandler<EdgeUICommands, EdgeUIEvents> {

    override suspend fun handle(command: EdgeUICommands): EdgeUIEvents? {
        return when (command) {
            ExitFromNetwork -> handleExitFromNetwork()
            RequestUpdatePeersCounter -> handleUpdatePeersCounter()
            is AddMatrixTask -> handlerMatrixTask(command)
            is GenerateMatrix -> null
        }
    }

    private suspend fun handleExitFromNetwork(): EdgeUIEvents? {
        edgeDomain.exitFromNetwork()
        return null
    }

    private suspend fun handleUpdatePeersCounter(): EdgeUIEvents {
        try {
            val peers = edgeDomain.updatePeersCounter()
            return UpdatePeersCounter(peers)
        } catch (error: Throwable) {
            return ShowInfo("Error update Peers Counter\n ${error.message}")
        }
    }

    private suspend fun handlerMatrixTask(command: AddMatrixTask): EdgeUIEvents? {
        val task = MatrixMultiply(
            id = command.params.getId(),
            params = command.params
        )
        edgeDomain.addTaskFromUI(task as EdgeTaskBasic)
        return null
    }
}