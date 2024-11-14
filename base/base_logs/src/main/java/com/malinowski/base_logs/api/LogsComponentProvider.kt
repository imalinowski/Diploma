package com.malinowski.base_logs.api

import com.malinowski.base_logs.api.di.LogsComponent

interface LogsComponentProvider {
    fun provideLogsComponent(): LogsComponent
}