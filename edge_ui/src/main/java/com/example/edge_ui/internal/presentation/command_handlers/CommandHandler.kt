package com.example.edge_ui.internal.presentation.command_handlers

internal interface CommandHandler<Command, Event> {
    fun handle(command: Command): Event?
}