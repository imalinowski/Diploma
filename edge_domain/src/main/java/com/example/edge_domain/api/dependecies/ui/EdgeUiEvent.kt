package com.example.edge_domain.api.dependecies.ui

import com.example.edge_entities.tasks.EdgeTaskBasic

sealed interface EdgeUiEvent {

    class NewTaskFromUI(
        task: EdgeTaskBasic
    ) : EdgeUiEvent

}