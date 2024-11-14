package com.malinowski.base_logs.api.di

import com.malinowski.base_logs.internal.di.LogsModule
import com.malinowski.base_logs.internal.view.LogFragment
import dagger.Subcomponent

@Subcomponent(
    modules = [
        LogsModule::class,
    ]
)
interface LogsComponent {

    fun inject(fragment: LogFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): LogsComponent
    }

}