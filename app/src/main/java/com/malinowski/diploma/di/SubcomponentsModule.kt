package com.malinowski.diploma.di

import com.example.edge_ui.api.di.EdgeUIComponent
import com.malinowski.chat.api.di.ChatComponent
import com.malinowski.logs.api.di.LogsComponent
import dagger.Module

@Module(
    subcomponents = [
        ChatComponent::class,
        EdgeUIComponent::class,
        LogsComponent::class
    ]
)
class SubcomponentsModule