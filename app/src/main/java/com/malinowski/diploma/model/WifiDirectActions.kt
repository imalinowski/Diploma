package com.malinowski.diploma.model

sealed class WifiDirectActions {
    class RequestPermissions(val permissions: Array<String>) : WifiDirectActions()
    class ShowToast(val text: String) : WifiDirectActions()
    class ShowAlertDialog(
        val title: String = "",
        val text: String,
        val dialogAction: () -> Unit = {}
    ) : WifiDirectActions()
    class OpenChat(val peer: WifiDirectPeer) : WifiDirectActions()
    class ReceiveMessage(val message: Message) : WifiDirectActions()

    class SaveLogs(val filename: String, val text: String): WifiDirectActions()
}