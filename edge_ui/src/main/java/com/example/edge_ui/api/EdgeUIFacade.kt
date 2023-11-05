package com.example.edge_ui.api

import android.content.Context
import android.content.Intent
import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_ui.internal.view.EdgeActivity
import com.example.edge_ui.internal.EdgeUiImpl

object EdgeUIFacade {

    fun getEdgeActivityIntent(context: Context): Intent {
        return EdgeActivity.createIntent(context)
    }

    fun provideEdgeUI(): EdgeUI {
        return EdgeUiImpl()
    }
}