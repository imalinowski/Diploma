package com.example.chat.internal.di

import com.example.chat.internal.view.ChatActivity
import com.example.chat.internal.view.ChatFragment
import com.example.chat.internal.view.LogFragment
import com.example.chat.internal.view.MainFragment
import com.example.chat.internal.view.PeerListFragment
import com.malinowski.diploma.di.ViewModelBuilderModule
import com.malinowski.diploma.di.WifiDirectModule
import dagger.Subcomponent

@Subcomponent(
    modules = [
        WifiDirectModule::class,
        ViewModelBuilderModule::class,
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