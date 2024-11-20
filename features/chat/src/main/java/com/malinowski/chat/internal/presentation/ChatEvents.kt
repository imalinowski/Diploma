package com.malinowski.chat.internal.presentation

import android.net.wifi.p2p.WifiP2pInfo
import com.example.wifi_direct.api.Message
import com.malinowski.chat.internal.model.ChatPeer

sealed interface ChatEvents {

    //chat
    data class SendMessage(val message: String) : ChatEvents

    data class NewMessage(val message: Message) : ChatEvents

    class OpenChat(val peer: ChatPeer) : ChatEvents

    sealed interface ChatUIEvents : ChatEvents {

        data object SearchForDevices : ChatUIEvents

        data class ConnectToPeer(
            val peer: ChatPeer
        ) : ChatUIEvents
    }

    // wifi direct
    sealed interface WifiDirectEvents : ChatEvents {

        data class PermissionMissed(
            val permissions: List<String>, val log: String
        ) : WifiDirectEvents

        data object PermissionsOkay : WifiDirectEvents

        data class WifiConnectionChanged(val info: WifiP2pInfo) : WifiDirectEvents

        data class ChatConnectionChanged(val connected: Boolean) : WifiDirectEvents

        data class PeersUpdate(val peers: List<ChatPeer>) : WifiDirectEvents
    }

    // common

    data class Error(val error: Throwable) : ChatEvents
}