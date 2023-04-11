package com.malinowski.diploma.di

import android.content.Context
import com.malinowski.diploma.view.*
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        WifiDirectModule::class,
        ViewModelBuilderModule::class,
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)
    fun inject(fragment: LogFragment)
    fun inject(fragment: PeerListFragment)
    fun inject(fragment: ChatFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }

}