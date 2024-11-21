package com.malinowski.chat.internal.presentation.command_handlers

import android.net.wifi.p2p.WifiP2pDevice
import com.example.common_arch.CommandHandler
import com.example.entities.HoursMinSec
import com.example.entities.getTime
import com.example.wifi_direct.api.DiscoverPeersResult
import com.example.wifi_direct.api.Message
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.internal.exceptions.ConnectionToPeerError
import com.malinowski.chat.internal.model.ChatPeer
import com.malinowski.chat.internal.presentation.ChatCommands
import com.malinowski.chat.internal.presentation.ChatCommands.ConnectPeer
import com.malinowski.chat.internal.presentation.ChatCommands.EnterNetwork
import com.malinowski.chat.internal.presentation.ChatCommands.ExitFromNetwork
import com.malinowski.chat.internal.presentation.ChatCommands.SearchPeers
import com.malinowski.chat.internal.presentation.ChatCommands.SendMessage
import com.malinowski.chat.internal.presentation.ChatEvents
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents.PeersUpdate
import javax.inject.Inject

class WifiDirectCommandHandler
@Inject constructor(
    private val wifiDirectCore: WifiDirectCore
) : CommandHandler<ChatCommands, ChatEvents> {

    override suspend fun handle(command: ChatCommands): ChatEvents? {
        return when (command) {
            EnterNetwork -> enterNetwork()
            ExitFromNetwork -> exitFromNetwork()
            SearchPeers -> searchForPeers()
            is ConnectPeer -> connectPeer(command.peer)
            is SendMessage -> sendMessage(command.message)
            else -> null
        }
    }

    private suspend fun searchForPeers(): ChatEvents {
        return when (val result = wifiDirectCore.discoverPeers()) {
            is DiscoverPeersResult.Peers -> PeersUpdate(mapPeers(result.peers))
            is DiscoverPeersResult.Error -> ChatEvents.Error(result.error)
        }
    }

    private fun mapPeers(
        peers: List<WifiP2pDevice>
    ): List<ChatPeer> {
        return peers.map { ChatPeer(it.deviceName, it.deviceAddress) }
    }

    private suspend fun connectPeer(
        peer: ChatPeer
    ): ChatEvents {
        return if (wifiDirectCore.connect(peer.address)) {
            ChatEvents.OpenChat(peer)
        } else {
            ChatEvents.Error(ConnectionToPeerError("Connect Failed"))
        }
    }

    private suspend fun sendMessage(
        text: String
    ): ChatEvents {
        return try {
            wifiDirectCore.sendMessage(text)
            val message = Message(
                text = text, fromRemote = false, time = getTime(HoursMinSec)
            )
            ChatEvents.NewMessage(message)
        } catch (error: Throwable) {
            ChatEvents.Error(error)
        }
    }

    private fun enterNetwork(): ChatEvents? {
        wifiDirectCore.registerReceiver()
        return null
    }

    private fun exitFromNetwork(): ChatEvents? {
        wifiDirectCore.unRegisterReceiver()
        return null
    }
}