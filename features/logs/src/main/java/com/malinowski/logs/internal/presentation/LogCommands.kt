package com.malinowski.logs.internal.presentation

sealed interface LogCommands {

    data object Update : LogCommands

    data object Clear : LogCommands

    data object Restore : LogCommands

    data class Save(
        val fileName: String
    ) : LogCommands

    data object SearchForDevices: LogCommands
}