package com.malinowski.diploma.model

import android.app.Activity
import android.app.Application
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