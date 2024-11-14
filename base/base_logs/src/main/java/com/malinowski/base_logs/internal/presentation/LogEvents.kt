package com.malinowski.base_logs.internal.presentation

sealed interface LogEvents {

    data class AddLog(val log: String) : LogEvents

    data class UpdateLog(val log: String) : LogEvents

    data object SaveLogs : LogEvents

    data object ClearLogs : LogEvents

    data object SearchForDevices : LogEvents
}