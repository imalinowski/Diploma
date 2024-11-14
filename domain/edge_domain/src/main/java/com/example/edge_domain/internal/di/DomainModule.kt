package com.example.edge_domain.internal.di

import com.example.edge_domain.api.EdgeDomain
import com.example.edge_domain.internal.EdgeDomainImpl
import com.example.edge_domain.internal.executor.EdgeTaskExecutor
import com.example.edge_domain.internal.executor.EdgeTaskExecutorImpl
import dagger.Binds
import dagger.Module

@Module(
    includes = [DomainModule.BindsModule::class]
)
class DomainModule {

    @Module
    internal interface BindsModule {

        @Binds
        fun getEdgeDomain(edgeData: EdgeDomainImpl): EdgeDomain

        @Binds
        fun getEdgeTaskExecutor(taskExecutor: EdgeTaskExecutorImpl): EdgeTaskExecutor
    }
}