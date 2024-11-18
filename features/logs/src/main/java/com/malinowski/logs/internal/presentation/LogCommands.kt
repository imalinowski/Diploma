package com.malinowski.logs.internal.presentation

sealed interface LogCommands {
    data class AddLog(
        val text: String
    ) : LogCommands

    data object Clear : LogCommands
    data object Restore : LogCommands
    data class Save(
        val fileName: String
    ) : LogCommands
}