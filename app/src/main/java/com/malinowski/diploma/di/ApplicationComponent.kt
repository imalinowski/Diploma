package com.malinowski.diploma.di

import android.content.Context
import com.example.edge_ui.api.di.EdgeUIComponent
import com.malinowski.chat.api.di.ChatComponent
import com.malinowski.logs.api.di.LogsComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        SubcomponentsModule::class,
        AppModule::class,
    ]
)
interface ApplicationComponent {

    fun chatComponent(): ChatComponent.Factory
    fun edgeUIComponent(): EdgeUIComponent.Factory
    fun logsComponent(): LogsComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}