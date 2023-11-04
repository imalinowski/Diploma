package com.example.multitaskfeature.internal

import com.example.multitask_domain.api.dependecies.ui.EdgeUI
import com.example.multitask_domain.api.dependecies.ui.EdgeUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class EdgeUiImpl : EdgeUI {
    override val eventsFlow: Flow<EdgeUiEvent> = flow {}

}