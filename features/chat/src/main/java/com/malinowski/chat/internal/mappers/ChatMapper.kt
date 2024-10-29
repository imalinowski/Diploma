package com.malinowski.chat.internal.mappers

import android.util.Log
import com.example.wifi_direct.api.WifiDirectEvents
import com.malinowski.chat.internal.presentation.ChatEvents
import javax.inject.Inject

class ChatMapper
@Inject constructor() : (WifiDirectEvents) -> ChatEvents {

    override operator fun invoke(
        event: WifiDirectEvents
    ): ChatEvents {
        return when (event) {
            is WifiDirectEvents.LogData ->
                ChatEvents.Log(event.log)

            is WifiDirectEvents.MessageData -> {
                ChatEvents.NewMessage(event.message)
            }

            is WifiDirectEvents.WifiConnectionChanged ->
                ChatEvents.WifiDirectEvents.WifiConnectionChanged(event.info)

            is WifiDirectEvents.SocketConnectionChanged ->
                ChatEvents.WifiDirectEvents.ChatConnectionChanged(event.connected)
        }
    }
}