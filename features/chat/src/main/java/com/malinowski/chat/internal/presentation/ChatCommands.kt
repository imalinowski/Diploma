package com.malinowski.chat.internal.presentation

import com.malinowski.chat.internal.model.ChatPeer

sealed interface ChatCommands {
    data object CheckPermissions : ChatCommands
    data object SearchPeers : ChatCommands
    data class ConnectPeer(
        val peer: ChatPeer
    ) : ChatCommands

    data class SendMessage(
        val message: String
    ) : ChatCommands

    sealed interface LogCommands : ChatCommands {
        data class AddLog(
            val text: String
        ) : LogCommands

        data object Clear : LogCommands
        data object Restore : LogCommands
        data class Save(
            val fileName: String
        ) : LogCommands
    }
}