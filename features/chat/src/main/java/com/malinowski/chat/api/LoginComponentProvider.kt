package com.malinowski.chat.api

import com.malinowski.chat.internal.di.ChatComponent

interface ChatComponentProvider {
    fun provideChatComponent(): ChatComponent
}