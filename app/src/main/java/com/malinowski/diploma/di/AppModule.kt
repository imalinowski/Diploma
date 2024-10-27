package com.malinowski.diploma.di

import com.example.common_arch.di.ViewModelBuilderModule
import com.example.edge_domain.api.EdgeDomain
import com.example.edge_domain.api.EdgeDomainFacade
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_domain.api.dependecies.data.EdgeData
import com.example.edge_ui.api.EdgeUIFacade
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

        @Binds
        fun getEdgeData(edgeData: WifiDirectDataImpl): EdgeData

    }

    @Provides
    fun provideEdgeDomain(
        edgeData: EdgeData
    ): EdgeDomain {
        val edgeDomainDependencies = object : EdgeDomainDependencies {
            override val edgeUi = EdgeUIFacade.provideEdgeUI()
            override val edgeData = edgeData
        }
        return EdgeDomainFacade.provideEdgeController(edgeDomainDependencies)
    }

}