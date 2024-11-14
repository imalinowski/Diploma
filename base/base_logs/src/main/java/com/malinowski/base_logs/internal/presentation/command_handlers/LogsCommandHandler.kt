package com.malinowski.base_logs.internal.presentation.command_handlers

import android.content.Context
import com.example.common_arch.CommandHandler
import com.malinowski.base_logs.api.Logs
import com.malinowski.base_logs.internal.presentation.LogCommands
import com.malinowski.base_logs.internal.presentation.LogEvents
import javax.inject.Inject

class LogsCommandHandler
@Inject constructor(
    private val logs: Logs,
    private val context: Context
) : CommandHandler<LogCommands, LogEvents> {

    override suspend fun handle(command: LogCommands): LogEvents? {
        when (command) {
            is LogCommands.AddLog -> logs.logData(command.text)
            LogCommands.Clear -> logs.clearLogs()
            is LogCommands.Save -> logs.saveLogs(context, command.fileName)
            LogCommands.Restore -> Unit // just return logs.data
        }
        return LogEvents.UpdateLog(logs.getLogs())
    }
}