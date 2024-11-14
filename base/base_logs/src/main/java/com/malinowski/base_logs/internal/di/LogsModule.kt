package com.malinowski.base_logs.internal.di

import dagger.Module

@Module(
    includes = [LogsModule.BindsModule::class]
)
class LogsModule {

    @Module
    interface BindsModule {

    }
}