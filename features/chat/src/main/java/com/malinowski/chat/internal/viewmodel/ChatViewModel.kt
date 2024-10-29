package com.malinowski.chat.internal.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.common_arch.Store
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.internal.ext.getTime
import com.malinowski.chat.internal.mappers.ChatMapper
import com.malinowski.chat.internal.model.ChatUiState
import com.malinowski.chat.internal.presentation.ChatCommands
import com.malinowski.chat.internal.presentation.ChatCommands.ConnectPeer
import com.malinowski.chat.internal.presentation.ChatCommands.SearchPeers
import com.malinowski.chat.internal.presentation.ChatEffects
import com.malinowski.chat.internal.presentation.ChatEffects.RequestPermissions
import com.malinowski.chat.internal.presentation.ChatEvents
import com.malinowski.chat.internal.presentation.ChatEvents.ChatUIEvents
import com.malinowski.chat.internal.presentation.ChatEvents.ChatUIEvents.ClearLogs
import com.malinowski.chat.internal.presentation.ChatEvents.ChatUIEvents.ConnectToPeer
import com.malinowski.chat.internal.presentation.ChatEvents.ChatUIEvents.SaveLogs
import com.malinowski.chat.internal.presentation.ChatEvents.ChatUIEvents.SearchForDevices
import com.malinowski.chat.internal.presentation.ChatEvents.Error
import com.malinowski.chat.internal.presentation.ChatEvents.Log
import com.malinowski.chat.internal.presentation.ChatEvents.NewMessage
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents.ChatConnectionChanged
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents.PeersUpdate
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents.PermissionMissed
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents.PermissionsOkay
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents.WifiConnectionChanged
import com.malinowski.chat.internal.presentation.command_handlers.PermissionsCommandHandler
import com.malinowski.chat.internal.presentation.command_handlers.WifiDirectCommandHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

// todo rewrite to common arch
class ChatViewModel @Inject constructor(
    private val wifiDirectCore: WifiDirectCore,
    permissionCommandHandler: PermissionsCommandHandler,
    wifiDirectCommandHandler: WifiDirectCommandHandler,
    chatMapper: ChatMapper
) : Store<ChatUiState, ChatCommands, ChatEvents, ChatEffects>(
    initialState = ChatUiState(),
    commandHandlers = listOf(
        permissionCommandHandler,
        wifiDirectCommandHandler,
    )
) {

    override val storeScope: CoroutineScope = viewModelScope

    init {
        wifiDirectCore.registerReceiver()
        storeScope.launch {
            wifiDirectCore.dataFlow.filterNotNull()
                .map(chatMapper)
                .collect(::dispatch)
        }
    }

    override fun dispatch(event: ChatEvents) {
        android.util.Log.i("RASPBERRY", "new event to chat $event")
        when (event) {
            is ChatEvents.OpenChat -> newEffect {
                ChatEffects.OpenChat(event.peer)
            }

            is ChatEvents.SendMessage -> command {
                ChatCommands.SendMessage(event.message)
            }

            is NewMessage -> newState {
                copy(messages = state.messages + listOf(event.message))
            }

            is Log -> newState {
                copy(logText = state.logText + "\n" + event.log)
            }

            is Error -> newEffect {
                showErrorAlertDialog(event.error)
            }

            is ChatUIEvents -> dispatchUIEvents(event)
            is WifiDirectEvents -> dispatchWifiDirectEvents(event)
        }
    }

    private fun dispatchUIEvents(event: ChatUIEvents) {
        when (event) {
            ClearLogs -> newState { copy(logText = "") }
            is ConnectToPeer -> command { ConnectPeer(event.peer) }
            SearchForDevices -> command { SearchPeers }
            SaveLogs -> newEffect { saveLogs() }
        }
    }

    private fun dispatchWifiDirectEvents(
        event: WifiDirectEvents
    ) {
        when (event) {
            PermissionsOkay -> command { SearchPeers }
            is PermissionMissed -> newEffect { RequestPermissions(event.permissions) }
            is PeersUpdate -> newState { copy(peers = event.peers) }
            is WifiConnectionChanged -> newState { copy(wifiConnectionInfo = event.info) }
            is ChatConnectionChanged -> newState { copy(chatConnectionInfo = event.connected) }
        }
    }

    private fun showErrorAlertDialog(error: Throwable): ChatEffects {
        return ChatEffects.ShowAlertDialog(
            title = error::class.java.name,
            text = error.message ?: "error"
        )
    }

    private fun saveLogs(): ChatEffects {
        return ChatEffects.SaveLogs(
            filename = "DIPLOMA_EXPERIMENT_${getTime()}",
            text = state.logText
        )
    }

    override fun onCleared() {
        super.onCleared()
        wifiDirectCore.unRegisterReceiver()
    }
}