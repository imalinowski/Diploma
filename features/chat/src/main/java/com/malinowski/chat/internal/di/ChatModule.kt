package com.malinowski.chat.internal.di

import androidx.lifecycle.ViewModel
import com.example.common_arch.di.ViewModelKey
import com.malinowski.chat.internal.viewmodel.ChatViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
    includes = [ChatModule.BindsModule::class]
)
class ChatModule {

    @Module
    interface BindsModule {

        @Binds
        @IntoMap
        @ViewModelKey(ChatViewModel::class)
        abstract fun bindChatViewModel(viewModel: ChatViewModel): ViewModel
    }
}