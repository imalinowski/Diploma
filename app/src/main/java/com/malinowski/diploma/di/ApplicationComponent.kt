package com.malinowski.diploma.di

import android.content.Context
import com.example.chat.internal.di.ChatComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        SubcomponentsModule::class
    ]
)
interface ApplicationComponent {

    fun chatComponent(): ChatComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }

}