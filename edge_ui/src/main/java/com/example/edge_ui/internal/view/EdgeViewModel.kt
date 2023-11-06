package com.example.edge_ui.internal.view

import androidx.lifecycle.ViewModel
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.command_handlers.CommandHandler
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal abstract class EdgeViewModel<Command : EdgeUICommands>(
    private val commandHandlers: List<CommandHandler> = listOf()
) : ViewModel() {

    abstract fun dispatch(event: EdgeUIEvents)

    private fun dispatchCommand(command: Command) {
        commandHandlers.forEach { commandHandler ->
            val event = commandHandler.handleCommand(command)
            event?.let { dispatch(event) }
        }
    }
}