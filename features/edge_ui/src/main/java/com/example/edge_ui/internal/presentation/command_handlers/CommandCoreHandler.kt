package com.example.edge_ui.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.edge_domain.api.EdgeDomain
import com.example.edge_entities.tasks.EdgeTaskBasic
import com.example.edge_entities.tasks.MatrixMultiply
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands.AddMatrixTask
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
internal class CommandCoreHandler
@Inject constructor(
    private val edgeDomain: EdgeDomain
) : CommandHandler<EdgeUICommands, EdgeUIEvents> {

    override suspend fun handle(command: EdgeUICommands): EdgeUIEvents? {
        if (command is AddMatrixTask) {
            val task = MatrixMultiply(
                id = command.params.getId(),
                params = command.params
            )
            edgeDomain.addTaskFromUI(task as EdgeTaskBasic)
        }
        return null
    }
}