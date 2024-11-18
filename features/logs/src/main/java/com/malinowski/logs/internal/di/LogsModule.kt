package com.malinowski.logs.internal.di

import androidx.lifecycle.ViewModel
import com.example.common_arch.di.ViewModelKey
import com.example.navigation.LogsNavigation
import com.malinowski.logs.internal.navigation.LogsNavigationImpl
import com.malinowski.logs.internal.view_model.LogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
    includes = [LogsModule.BindsModule::class]
)
class LogsModule {

    @Module
    interface BindsModule {

        @Binds
        fun bindLogNavigation(impl: LogsNavigationImpl): LogsNavigation

        @Binds
        @IntoMap
        @ViewModelKey(LogViewModel::class)
        abstract fun bindLogViewModel(viewModel: LogViewModel): ViewModel
    }
}