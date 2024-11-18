package com.malinowski.logs.api

import com.malinowski.logs.api.di.LogsComponent

interface LogsComponentProvider {
    fun provideLogsComponent(): LogsComponent
}