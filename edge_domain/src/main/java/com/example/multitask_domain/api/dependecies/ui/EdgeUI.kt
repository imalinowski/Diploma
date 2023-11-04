package com.example.multitask_domain.api.dependecies.ui

import kotlinx.coroutines.flow.Flow

interface EdgeUI {
    val eventsFlow: Flow<EdgeUiEvent> // поток обратой связи с UI
}