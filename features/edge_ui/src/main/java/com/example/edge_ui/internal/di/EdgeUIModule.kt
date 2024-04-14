package com.example.edge_ui.internal.di

import androidx.lifecycle.ViewModel
import com.example.common_arch.di.ViewModelKey
import com.example.edge_ui.internal.view.EdgeUIViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
    includes = [EdgeUIModule.BindsModule::class]
)
internal class EdgeUIModule {

    @Module
    interface BindsModule {

        @Binds
        @IntoMap
        @ViewModelKey(EdgeUIViewModel::class)
        abstract fun bindEdgeUIViewModel(viewModel: EdgeUIViewModel): ViewModel

    }

}