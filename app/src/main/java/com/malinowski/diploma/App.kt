package com.malinowski.diploma

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.malinowski.diploma.di.ApplicationComponent
import com.malinowski.diploma.di.DaggerApplicationComponent

class App : Application() {

    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this)
    }
}

fun Activity.getComponent(): ApplicationComponent = (application as App).appComponent
fun Fragment.getComponent(): ApplicationComponent = requireActivity().getComponent()