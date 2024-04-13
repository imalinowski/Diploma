package com.malinowski.diploma

import android.app.Application
import com.example.chat.api.ChatComponentProvider
import com.example.chat.internal.di.ChatComponent
import com.malinowski.diploma.di.ApplicationComponent
import com.malinowski.diploma.di.DaggerApplicationComponent

class App : Application(), ChatComponentProvider {

    private lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this)
    }

    override fun provideChatComponent(): ChatComponent {
        return appComponent.chatComponent().create()
    }
}
