package com.malinowski.diploma.di

import com.example.common_arch.di.ViewModelBuilderModule
import com.example.edge_data.api.EdgeDataDependencies
import com.example.edge_data.api.EdgeDataFacade
import com.example.edge_domain.api.EdgeDomain
import com.example.edge_domain.api.EdgeDomainFacade
import com.example.edge_domain.api.dependecies.EdgeDomainDependencies
import com.example.edge_ui.api.EdgeUIFacade
import dagger.Module
import dagger.Provides

@Module(
    includes = [
        AppModule.BindsModule::class,
        ViewModelBuilderModule::class,
    ]
)
class AppModule {

    @Module
    interface BindsModule {

    }

    @Provides
    fun provideEdgeDomain(): EdgeDomain {
        val edgeDomainDependencies = object : EdgeDomainDependencies {
            override val edgeUi = EdgeUIFacade.provideEdgeUI()
            override val edgeData = EdgeDataFacade.provideEdgeData(
                object : EdgeDataDependencies {
                    override val deviceName: String
                        get() = android.os.Build.MODEL
                }
            )
        }
        return EdgeDomainFacade.provideEdgeController(edgeDomainDependencies)
    }

}