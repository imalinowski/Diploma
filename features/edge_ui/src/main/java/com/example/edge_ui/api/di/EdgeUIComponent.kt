package com.example.edge_ui.api.di

import com.example.edge_ui.internal.di.EdgeUIModule
import com.example.edge_ui.internal.view.EdgeActivity
import dagger.Subcomponent

@Subcomponent(
    modules = [
        EdgeUIModule::class,
    ]
)
interface EdgeUIComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): EdgeUIComponent
    }

    fun inject(activity: EdgeActivity)

}