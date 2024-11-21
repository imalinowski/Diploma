package com.example.entities

import javax.inject.Inject
import javax.inject.Singleton

// Это класс со всеми логами приложения показывается в LogFragment
// !!! пока нет потребности делать Thread Save но это не точно

private const val LINE_SEPARATOR = "\n"
private const val MAX_LOG_SIZE = 500

@Singleton
class Logs
@Inject constructor(

) {

    private var logs: String = ""

    fun getLogs() = logs

    fun logData(log: String) {
        synchronized(logs) {
            val shortedLog = if (log.length > MAX_LOG_SIZE) {
                "${log.take(MAX_LOG_SIZE)}..."
            } else {
                log
            }
            logs += LINE_SEPARATOR + getTime(MinSec) + LINE_SEPARATOR + shortedLog
        }
    }

    fun clearLogs() {
        logs = ""
    }
}