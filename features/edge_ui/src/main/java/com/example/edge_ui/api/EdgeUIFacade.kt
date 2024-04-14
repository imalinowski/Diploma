package com.example.edge_ui.api

import android.content.Context
import android.content.Intent
import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_ui.internal.domain.EdgeUiImpl
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.view.EdgeActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

object EdgeUIFacade {

    private val eventsToUI = MutableSharedFlow<EdgeUIEvents>()
    private val edgeUi by lazy {
        EdgeUiImpl(eventsToUI)
    }

    fun getEdgeActivityIntent(context: Context): Intent {
        return EdgeActivity.createIntent(context)
    }

    fun provideEdgeUI(): EdgeUI {
        return edgeUi
    }

    internal fun provideEventsToUIFlow(): Flow<EdgeUIEvents> {
        return eventsToUI
    }
}