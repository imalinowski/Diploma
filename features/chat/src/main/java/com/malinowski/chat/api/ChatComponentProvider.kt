package com.malinowski.chat.api

import com.malinowski.chat.api.di.ChatComponent

interface ChatComponentProvider {
    fun provideChatComponent(): ChatComponent
}