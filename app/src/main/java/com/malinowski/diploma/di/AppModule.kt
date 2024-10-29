package com.malinowski.diploma.di

import com.example.common_arch.di.ViewModelBuilderModule
import com.example.edge_domain.api.EdgeDomain
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_domain.api.dependecies.ui.EdgeUI
import com.example.edge_domain.internal.EdgeDomainImpl
import com.example.edge_domain.internal.executor.EdgeTaskExecutor
import com.example.edge_domain.internal.executor.EdgeTaskExecutorImpl
import com.example.edge_ui.internal.domain.EdgeUiImpl
import com.example.wifi_direct.internal.di.WifiDirectModule
import com.malinowski.wifi_direct_data.internal.WifiDirectDataImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(
    includes = [
        AppModule.BindsModule::class,
        ViewModelBuilderModule::class,
        WifiDirectModule::class
    ]
)
class AppModule {

    @Module
    interface BindsModule {

        // todo move to ui module

        @Binds
        fun getEdgeUI(edgeData: EdgeUiImpl): EdgeUI

        // todo move to data module

        @Binds
        fun getEdgeData(edgeData: WifiDirectDataImpl): EdgeData

        // todo movve to domain module
        @Binds
        fun getEdgeDomain(edgeData: EdgeDomainImpl): EdgeDomain

        @Binds
        fun getEdgeTaskExecutor(taskExecutor: EdgeTaskExecutorImpl): EdgeTaskExecutor

    }

    @Provides
    fun provideEdgeDomain(
        edgeUI: EdgeUI,
        edgeData: EdgeData,
    ): EdgeDomainDependencies {
        return object : EdgeDomainDependencies {
            override val edgeUi = edgeUI
            override val edgeData = edgeData
        }
    }

}