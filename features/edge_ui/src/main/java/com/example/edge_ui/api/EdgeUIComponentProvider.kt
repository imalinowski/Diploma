package com.example.edge_ui.api

import com.example.edge_ui.api.di.EdgeUIComponent

interface EdgeUIComponentProvider {
    fun provideEdgeUIComponent(): EdgeUIComponent
}