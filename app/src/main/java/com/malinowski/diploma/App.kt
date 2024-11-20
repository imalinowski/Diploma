package com.malinowski.diploma

import android.app.Application
import com.example.edge_ui.api.EdgeUIComponentProvider
import com.example.edge_ui.api.di.EdgeUIComponent
import com.malinowski.chat.api.ChatComponentProvider
import com.malinowski.chat.api.di.ChatComponent
import com.malinowski.diploma.di.ApplicationComponent
import com.malinowski.diploma.di.DaggerApplicationComponent
import com.malinowski.logs.api.LogsComponentProvider
import com.malinowski.logs.api.di.LogsComponent

class App : Application(), ChatComponentProvider, EdgeUIComponentProvider, LogsComponentProvider {

    private lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this)
    }

    override fun provideEdgeUIComponent(): EdgeUIComponent {
        return appComponent.edgeUIComponent().create()
    }

    override fun provideChatComponent(): ChatComponent {
        return appComponent.chatComponent().create()
    }

    override fun provideLogsComponent(): LogsComponent {
        return appComponent.logsComponent().create()
    }
}
