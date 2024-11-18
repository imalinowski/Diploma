package com.malinowski.logs.internal.navigation

import androidx.fragment.app.Fragment
import com.example.navigation.LogsNavigation
import com.malinowski.logs.internal.view.LogFragment
import javax.inject.Inject

class LogsNavigationImpl
@Inject constructor() : LogsNavigation {

    override fun createLogsFragment(): Fragment {
        return LogFragment.newInstance()
    }
}