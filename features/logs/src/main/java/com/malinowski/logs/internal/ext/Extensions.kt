package com.malinowski.logs.internal.ext

import android.app.Activity
import androidx.fragment.app.Fragment
import com.malinowski.logs.api.LogsComponentProvider
import com.malinowski.logs.api.di.LogsComponent

fun Activity.getComponent(): LogsComponent = (applicationContext as LogsComponentProvider).provideLogsComponent()

fun Fragment.getComponent(): LogsComponent = requireActivity().getComponent()