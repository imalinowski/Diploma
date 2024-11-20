package com.malinowski.chat.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.entities.Logs
import com.malinowski.chat.internal.presentation.ChatCommands
import com.malinowski.chat.internal.presentation.ChatCommands.LogCommands
import com.malinowski.chat.internal.presentation.ChatCommands.LogCommands.AddLog
import com.malinowski.chat.internal.presentation.ChatCommands.LogCommands.Clear
import com.malinowski.chat.internal.presentation.ChatCommands.LogCommands.Restore
import com.malinowski.chat.internal.presentation.ChatCommands.LogCommands.Save
import com.malinowski.chat.internal.presentation.ChatEvents
import javax.inject.Inject

class LogsCommandHandler
@Inject constructor(
    private val logs: Logs,
) : CommandHandler<ChatCommands, ChatEvents> {

    override suspend fun handle(command: ChatCommands): ChatEvents? {
        if (command !is LogCommands) {
            return null
        }
        when (command) {
            is AddLog -> Unit //logs.logData(command.text)
            Clear -> logs.clearLogs()
            is Save -> Unit // logs.saveLogs(context, command.fileName)
            Restore -> Unit // just return logs.data
        }
        return ChatEvents.LogEvents.UpdateLog(logs.getLogs())
    }
}