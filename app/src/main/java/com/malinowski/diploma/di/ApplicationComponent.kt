package com.malinowski.diploma.di

import android.content.Context
import com.malinowski.diploma.view.LogFragment
import com.malinowski.diploma.view.MainActivity
import com.malinowski.diploma.view.PeerListFragment
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
    fun inject(fragment: LogFragment)
    fun inject(fragment: PeerListFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }

}