package com.example.edge_data.internal.di

import dagger.Module

@Module(
    includes = [EdgeDataModule.BindsModule::class]
)
class EdgeDataModule {

    @Module
    interface BindsModule {

    }

}