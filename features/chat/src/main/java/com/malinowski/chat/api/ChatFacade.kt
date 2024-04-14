package com.malinowski.chat.api

import android.content.Context
import android.content.Intent
import com.malinowski.chat.internal.view.ChatActivity

object ChatFacade {

    fun getChatActivityIntent(context: Context): Intent {
        return ChatActivity.createIntent(context)
    }
}