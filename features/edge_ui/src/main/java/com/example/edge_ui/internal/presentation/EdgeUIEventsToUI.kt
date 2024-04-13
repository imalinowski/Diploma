package com.example.edge_ui.internal.presentation

internal sealed interface EdgeUIEventsToUI {

    class ShowToast(
        val text: String
    ) : EdgeUIEventsToUI

}