package com.example.chat.api

import com.example.chat.internal.di.ChatComponent

interface ChatComponentProvider {
    fun provideChatComponent(): ChatComponent
}