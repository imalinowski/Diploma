package com.example.edge_ui.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.edge_entities.tasks.EdgeTaskBasic
import com.example.edge_entities.tasks.MatrixMultiply
import com.example.edge_ui.internal.domain.provideEdgeDomainController
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask

@Suppress("UNCHECKED_CAST")
internal class CommandCoreHandler : CommandHandler<EdgeUICommands, EdgeUIEvents> {

    private val domainController = provideEdgeDomainController()

    override fun handle(command: EdgeUICommands): EdgeUIEvents? {
        if (command is AddMatrixTask) {
            val task = MatrixMultiply(
                id = command.params.getId(),
                params = command.params
            )
            domainController.addTask(task as EdgeTaskBasic)
        }
        return null
    }
}