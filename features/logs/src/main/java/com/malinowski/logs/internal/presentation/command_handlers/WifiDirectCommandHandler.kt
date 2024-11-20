package com.malinowski.logs.internal.presentation.command_handlers

import com.example.common_arch.CommandHandler
import com.example.wifi_direct.api.WifiDirectCore
import com.malinowski.logs.internal.presentation.LogCommands
import com.malinowski.logs.internal.presentation.LogEvents
import javax.inject.Inject

class WifiDirectCommandHandler
@Inject constructor(
    private val wifiDirectCore: WifiDirectCore,
) : CommandHandler<LogCommands, LogEvents> {

    override suspend fun handle(command: LogCommands): LogEvents? {
        return when (command) {
            LogCommands.SearchForDevices -> {
                discoverPeers()
            }

            else -> null
        }
    }

    private suspend fun discoverPeers(): LogEvents? {
        try {
            wifiDirectCore.discoverPeers()
        } catch (e: Throwable) {
            // do nothing
        }
        return null
    }
}