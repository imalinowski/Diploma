package com.malinowski.logs.internal.presentation

sealed interface LogEvents {

    data class UpdateLog(val log: String) : LogEvents

    data object NewLog : LogEvents

    data object SaveLogs : LogEvents

    data object ClearLogs : LogEvents

    data object SearchForDevices : LogEvents

    data class ShowToast(val text: String) : LogEvents
}