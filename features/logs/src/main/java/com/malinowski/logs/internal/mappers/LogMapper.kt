package com.malinowski.logs.internal.mappers

import com.example.wifi_direct.api.WifiDirectEvents
import com.malinowski.logs.internal.presentation.LogEvents
import javax.inject.Inject

class LogMapper
@Inject constructor() : (WifiDirectEvents?) -> LogEvents? {

    override operator fun invoke(
        event: WifiDirectEvents?
    ): LogEvents? {
        return when (event) {
            is WifiDirectEvents.LogData ->
                LogEvents.AddLog(event.log)

            else -> null
        }
    }
}