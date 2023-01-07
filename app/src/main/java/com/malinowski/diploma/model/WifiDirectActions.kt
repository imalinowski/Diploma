package com.malinowski.diploma.model

sealed class WifiDirectActions {
    class RequestPermissions(val permissions: Array<String>) : WifiDirectActions()
    class ShowToast(val text: String) : WifiDirectActions()
}