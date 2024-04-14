package com.malinowski.diploma.di

import com.malinowski.chat.internal.di.ChatComponent
import dagger.Module

@Module(
    subcomponents = [
        ChatComponent::class
    ]
)
class SubcomponentsModule