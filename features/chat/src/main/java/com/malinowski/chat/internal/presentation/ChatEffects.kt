package com.malinowski.chat.internal.presentation

import com.example.wifi_direct.api.Message
import com.malinowski.chat.internal.model.ChatPeer

sealed class ChatEffects {

    class RequestPermissions(val permissions: List<String>) : ChatEffects()

    class ShowToast(val text: String) : ChatEffects()

    class ShowAlertDialog(
        val title: String = "",
        val text: String,
        val dialogAction: () -> Unit = {}
    ) : ChatEffects()

    class OpenChat(val peer: ChatPeer) : ChatEffects()

    class ReceiveMessage(val message: Message) : ChatEffects()

    class SaveLogs(val filename: String, val text: String) : ChatEffects()
}