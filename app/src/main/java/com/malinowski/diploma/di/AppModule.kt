package com.malinowski.diploma.di

import com.example.common_arch.di.ViewModelBuilderModule
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.internal.di.DomainModule
import com.example.edge_ui.internal.di.EdgeUIModule
import com.example.wifi_direct.internal.di.WifiDirectModule
import com.malinowski.logs.internal.di.LogsModule
import com.malinowski.wifi_direct_data.internal.WifiDirectDataImpl
import dagger.Binds
import dagger.Module

@Module(
    includes = [
        AppModule.BindsModule::class,
        ViewModelBuilderModule::class,
        WifiDirectModule::class,
        DomainModule::class,
        EdgeUIModule::class,
        LogsModule::class,
    ]
)
class AppModule {

    @Module
    interface BindsModule {

        // todo move to data module

        @Binds
        fun getEdgeData(edgeData: WifiDirectDataImpl): EdgeData
    }
}