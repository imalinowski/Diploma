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

    data object EnterNetwork: ChatCommands

    data object ExitFromNetwork: ChatCommands
}