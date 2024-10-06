package com.example.edge_ui.internal.presentation

internal sealed interface EdgeUIEffects {

    class ShowToast(
        val text: String
    ) : EdgeUIEffects

}