package com.malinowski.chat.api.di

import com.malinowski.chat.internal.di.WifiDirectModule
import com.malinowski.chat.internal.view.ChatActivity
import com.malinowski.chat.internal.view.ChatFragment
import com.malinowski.chat.internal.view.LogFragment
import com.malinowski.chat.internal.view.MainFragment
import com.malinowski.chat.internal.view.PeerListFragment
import dagger.Subcomponent

@Subcomponent(
    modules = [
        WifiDirectModule::class,
    ]
)
interface ChatComponent {

    fun inject(activity: ChatActivity)
    fun inject(fragment: MainFragment)
    fun inject(fragment: LogFragment)
    fun inject(fragment: PeerListFragment)
    fun inject(fragment: ChatFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatComponent
    }

}