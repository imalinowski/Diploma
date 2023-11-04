package com.example.multitaskfeature.api

import android.content.Context
import android.content.Intent
import com.example.multitask_domain.api.dependecies.ui.EdgeUI
import com.example.multitaskfeature.internal.EdgeActivity
import com.example.multitaskfeature.internal.EdgeUiImpl

object EdgeUIFacade {

    fun getEdgeActivityIntent(context: Context): Intent {
        return EdgeActivity.createIntent(context)
    }

    fun provideEdgeUI(): EdgeUI {
        return EdgeUiImpl()
    }
}