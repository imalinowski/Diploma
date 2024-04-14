package com.malinowski.diploma.di

import com.example.edge_ui.api.di.EdgeUIComponent
import com.malinowski.chat.api.di.ChatComponent
import dagger.Module

@Module(
    subcomponents = [
        ChatComponent::class,
        EdgeUIComponent::class,
    ]
)
class SubcomponentsModule