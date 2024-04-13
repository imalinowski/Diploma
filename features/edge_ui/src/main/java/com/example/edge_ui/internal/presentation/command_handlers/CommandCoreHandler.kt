package com.example.edge_ui.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.edge_entities.tasks.EdgeTaskBasic
import com.example.edge_entities.tasks.MatrixMultiply
import com.example.edge_ui.api.EdgeUIFacade
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask

@Suppress("UNCHECKED_CAST")
internal class CommandCoreHandler : CommandHandler<EdgeUICommands, EdgeUIEvents> {

    private val domainController = EdgeUIFacade.getDomainController()

    override suspend fun handle(command: EdgeUICommands): EdgeUIEvents? {
        if (command is AddMatrixTask) {
            val task = MatrixMultiply(
                id = command.params.getId(),
                params = command.params
            )
            domainController.addTaskFromUI(task as EdgeTaskBasic)
        }
        return null
    }
}