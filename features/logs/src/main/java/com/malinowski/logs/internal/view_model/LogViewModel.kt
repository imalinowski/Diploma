package com.malinowski.logs.internal.view_model

import androidx.lifecycle.viewModelScope
import com.example.common_arch.Store
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.internal.ext.getTime
import com.malinowski.logs.internal.mappers.LogMapper
import com.malinowski.logs.internal.presentation.LogCommands
import com.malinowski.logs.internal.presentation.LogEffects
import com.malinowski.logs.internal.presentation.LogEvents
import com.malinowski.logs.internal.presentation.LogEvents.AddLog
import com.malinowski.logs.internal.presentation.LogEvents.ClearLogs
import com.malinowski.logs.internal.presentation.LogEvents.SaveLogs
import com.malinowski.logs.internal.presentation.LogEvents.UpdateLog
import com.malinowski.logs.internal.presentation.LogUiState
import com.malinowski.logs.internal.presentation.command_handlers.LogsCommandHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogViewModel @Inject constructor(
    private val wifiDirectCore: WifiDirectCore,
    logsCommandHandler: LogsCommandHandler,
    logMapper: LogMapper
) : Store<LogUiState, LogCommands, LogEvents, LogEffects>(
    initialState = LogUiState(),
    commandHandlers = listOf(
        logsCommandHandler
    )
) {

    override val storeScope: CoroutineScope = viewModelScope

    init {
        wifiDirectCore.registerReceiver()
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
            is AddLog -> command {
                LogCommands.AddLog(event.log)
            }

            is UpdateLog -> newState {
                state.copy(logText = event.log)
            }

            SaveLogs -> command {
                saveLogs()
            }

            ClearLogs -> command {
                LogCommands.Clear
            }

            LogEvents.SearchForDevices -> TODO()
        }
    }

    private fun saveLogs(): LogCommands {
        return LogCommands.Save(fileName = "DIPLOMA_EXPERIMENT_${getTime()}")
    }

    override fun onCleared() {
        super.onCleared()
        wifiDirectCore.unRegisterReceiver()
    }
}