package com.malinowski.base_logs.internal.ext

import android.app.Activity
import androidx.fragment.app.Fragment
import com.malinowski.base_logs.api.LogsComponentProvider
import com.malinowski.base_logs.api.di.LogsComponent

fun Activity.getComponent(): LogsComponent = (applicationContext as LogsComponentProvider).provideLogsComponent()

fun Fragment.getComponent(): LogsComponent = requireActivity().getComponent()