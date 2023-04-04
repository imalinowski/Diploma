package com.malinowski.diploma.model

sealed class WifiDirectActions {
    class RequestPermissions(val permissions: Array<String>) : WifiDirectActions()
    class ShowToast(val text: String) : WifiDirectActions()
    class ShowAlertDialog(
        val title: String = "",
        val text: String,
        val dialogAction: () -> Unit = {}
    ) : WifiDirectActions()

    object OpenChat : WifiDirectActions()
}