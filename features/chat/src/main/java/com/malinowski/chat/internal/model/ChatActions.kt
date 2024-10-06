package com.malinowski.chat.internal.model

import com.example.wifi_direct.api.Message

sealed class ChatActions {

    class RequestPermissions(val permissions: Array<String>) : ChatActions()

    class ShowToast(val text: String) : ChatActions()

    class ShowAlertDialog(
        val title: String = "",
        val text: String,
        val dialogAction: () -> Unit = {}
    ) : ChatActions()

    class OpenChat(val peer: ChatPeer) : ChatActions()

    class ReceiveMessage(val message: Message) : ChatActions()

    class SaveLogs(val filename: String, val text: String) : ChatActions()
}