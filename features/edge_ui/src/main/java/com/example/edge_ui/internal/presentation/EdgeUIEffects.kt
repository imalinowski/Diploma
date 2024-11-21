package com.example.edge_ui.internal.presentation

internal sealed interface EdgeUIEffects {

    data class ShowToast(
        val text: String
    ) : EdgeUIEffects

    data class ShowAlertView(
        val title: String,
        val text: String,
        val action: () -> Unit = {},
    ) : EdgeUIEffects
}