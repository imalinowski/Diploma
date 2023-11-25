package com.example.edge_ui.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.edge_entities.tasks.MatrixMultiply
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask
import com.example.edge_ui.internal.provideEdgeDomainController

internal class CommandCoreHandler : CommandHandler<EdgeUICommands, EdgeUIEvents> {

    private val domainController = provideEdgeDomainController()

    override fun handle(command: EdgeUICommands): EdgeUIEvents? {
        if (command is AddMatrixTask) {
            val task = MatrixMultiply(command.params)
            domainController.addTask(task)
        }
        return null
    }
}