package com.malinowski.chat.internal.presentation

import com.malinowski.chat.internal.model.ChatPeer

sealed class ChatCommands {
    data object CheckPermissions : ChatCommands()
    data object SearchPeers : ChatCommands()
    data class ConnectPeer(val peer: ChatPeer) : ChatCommands()
    data class SendMessage(val message: String) : ChatCommands()
}