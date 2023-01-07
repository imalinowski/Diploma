package com.malinowski.diploma.di

import android.content.Context
import com.malinowski.diploma.view.WifiDirectActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        WifiDirectModule::class,
        ViewModelBuilderModule::class,
    ]
)
interface ApplicationComponent {

    fun inject(activity: WifiDirectActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }

}