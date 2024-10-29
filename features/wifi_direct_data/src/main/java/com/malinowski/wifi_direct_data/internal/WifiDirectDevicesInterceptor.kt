package com.malinowski.wifi_direct_data.internal

import com.example.edge_entities.EdgeDevice
import com.example.wifi_direct.api.DiscoverPeersResult.Error
import com.example.wifi_direct.api.DiscoverPeersResult.Peers
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.api.WifiDirectEvents.MessageData
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessage
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessageType.Ack
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessageType.Syn
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val AWAIT_TIMEOUT_MLS = 1000L

class WifiDirectDevicesInterceptor
@Inject constructor(
    private val wifiDirectCore: WifiDirectCore
) {

    suspend fun getOnlineDevices(): List<EdgeDevice> {
        val candidates = when (
            val result = wifiDirectCore.discoverPeers()
        ) {
            is Error -> throw result.error
            is Peers -> result.peers.map {
                EdgeDevice(it.deviceName, it.deviceAddress)
            }
        }
        return candidates.filter {
            checkCandidate(it)
        }
    }

    private suspend fun checkCandidate(candidate: EdgeDevice): Boolean {
        if (wifiDirectCore.connect(candidate.requireAddress()).not()) {
            return false
        }
        val syn = WifiDirectTaskMessage(type = Syn, content = "")
        wifiDirectCore.sendMessage(Json.encodeToString(syn))
        return wifiDirectCore.awaitAck()
    }

    private suspend fun WifiDirectCore.awaitAck(): Boolean {
        return withTimeoutOrNull(AWAIT_TIMEOUT_MLS) {
            val message = dataFlow.filterIsInstance<MessageData>().first()
            val ack = Json.decodeFromString<WifiDirectTaskMessage>(message.message.text)
            ack.type == Ack
        } ?: false
    }

    suspend fun interceptSyn(
        message: MessageData
    ) {
        try {
            val syn = Json.decodeFromString<WifiDirectTaskMessage>(message.message.text)
            if (syn.type == Syn) {
                val ack = WifiDirectTaskMessage(type = Ack, content = "")
                wifiDirectCore.sendMessage(Json.encodeToString(ack))
            }
        } catch (_: Throwable) {
            // todo figure out why message wrong
        }
    }
}