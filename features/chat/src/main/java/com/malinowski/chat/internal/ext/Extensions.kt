package com.malinowski.chat.internal.ext

import android.app.Activity
import androidx.fragment.app.Fragment
import com.malinowski.chat.api.ChatComponentProvider
import com.malinowski.chat.api.di.ChatComponent

fun Activity.getComponent(): ChatComponent = (applicationContext as ChatComponentProvider).provideChatComponent()
fun Fragment.getComponent(): ChatComponent = requireActivity().getComponent()