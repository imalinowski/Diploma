package com.example.edge_data.api.di

import com.example.edge_data.internal.di.EdgeDataModule
import dagger.Subcomponent

@Subcomponent(
    modules = [
        EdgeDataModule::class,
    ]
)
interface EdgeDataComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): EdgeDataComponent
    }

}