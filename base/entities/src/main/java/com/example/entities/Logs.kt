package com.example.entities

import javax.inject.Inject
import javax.inject.Singleton

// Это класс со всеми логами приложения показывается в LogFragment
// !!! пока нет потребности делать Thread Save но это не точно

private const val LINE_SEPARATOR = "\n"

@Singleton
class Logs
@Inject constructor() {

    private var logs: String = ""

    fun getLogs() = logs

    fun logData(log: String) {
        logs += LINE_SEPARATOR + log
    }

    fun clearLogs() {
        logs = ""
    }
}