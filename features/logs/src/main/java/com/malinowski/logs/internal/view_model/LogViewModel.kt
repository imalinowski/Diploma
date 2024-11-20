package com.malinowski.logs.internal.view_model

import androidx.lifecycle.viewModelScope
import com.example.common_arch.Store
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.internal.ext.getTime
import com.malinowski.logs.internal.mappers.LogMapper
import com.malinowski.logs.internal.presentation.LogCommands
import com.malinowski.logs.internal.presentation.LogEffects
import com.malinowski.logs.internal.presentation.LogEvents
import com.malinowski.logs.internal.presentation.LogEvents.ClearLogs
import com.malinowski.logs.internal.presentation.LogEvents.NewLog
import com.malinowski.logs.internal.presentation.LogEvents.SaveLogs
import com.malinowski.logs.internal.presentation.LogEvents.UpdateLog
import com.malinowski.logs.internal.presentation.LogUiState
import com.malinowski.logs.internal.presentation.command_handlers.LogsCommandHandler
import com.malinowski.logs.internal.presentation.command_handlers.WifiDirectCommandHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogViewModel @Inject constructor(
    wifiDirectCore: WifiDirectCore,
    logsCommandHandler: LogsCommandHandler,
    wifiDirectCommandHandler: WifiDirectCommandHandler,
    logMapper: LogMapper
) : Store<LogUiState, LogCommands, LogEvents, LogEffects>(
    initialState = LogUiState(),
    commandHandlers = listOf(
        logsCommandHandler,
        wifiDirectCommandHandler
    )
) {

    override val storeScope: CoroutineScope = viewModelScope

    init {
        storeScope.launch {
            wifiDirectCore.dataFlow
                .map(logMapper)
                .filterNotNull()
                .collect(::dispatch)
        }
        command { LogCommands.Restore }
    }

    override fun dispatch(event: LogEvents) {
        when (event) {
            is UpdateLog -> newState {
                state.copy(logText = event.log)
            }

            is NewLog -> command {
                LogCommands.Update
            }

            SaveLogs -> command {
                saveLogs()
            }

            ClearLogs -> command {
                LogCommands.Clear
            }

            LogEvents.SearchForDevices -> command {
                LogCommands.SearchForDevices
            }
        }
    }

    private fun saveLogs(): LogCommands {
        return LogCommands.Save(fileName = "DIPLOMA_EXPERIMENT_${getTime()}")
    }
}