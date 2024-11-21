package com.malinowski.wifi_direct_data.internal

import com.example.entities.EdgeDevice
import com.example.entities.Logs
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

private const val AWAIT_TIMEOUT_MLS = 5000L

class WifiDirectDevicesInterceptor
@Inject constructor(
    private val logs: Logs,
    private val wifiDirectCore: WifiDirectCore
) {

    suspend fun getOnlineDevices(): List<EdgeDevice> {
        logs.logData("Update counter : launch search for candidates... ")
        val candidates = when (
            val result = wifiDirectCore.discoverPeers()
        ) {
            is Error -> throw result.error
            is Peers -> result.peers.map {
                EdgeDevice(it.deviceName, it.deviceAddress)
            }
        }
        logs.logData("Update counter : candidates ${candidates.size}")
        return candidates.filter {
            checkCandidate(it)
        }
    }

    private suspend fun checkCandidate(candidate: EdgeDevice): Boolean {
        try {
            logs.logData("Update counter : check candidate $candidate")
            if (wifiDirectCore.connect(candidate.requireAddress()).not()) {
                return false
            }
            val syn = WifiDirectTaskMessage(type = Syn, content = "")
            wifiDirectCore.sendMessage(Json.encodeToString(syn))
            logs.logData("Update counter : wait for ack from candidate $candidate")
            return wifiDirectCore.awaitAck()
        } catch (error: Throwable) {
            logs.logData("Update counter : candidate $candidate cause error \n ${error.message}")
            return false
        }
    }

    private suspend fun WifiDirectCore.awaitAck(): Boolean {
        return withTimeoutOrNull(AWAIT_TIMEOUT_MLS) {
            val message = dataFlow.filterIsInstance<MessageData>().first()
            val ack = Json.decodeFromString<WifiDirectTaskMessage>(message.message.text)
            ack.type == Ack
        } ?: false
    }

    suspend fun tryInterceptSyn(
        message: MessageData
    ) {
        try {
            val syn = Json.decodeFromString<WifiDirectTaskMessage>(message.message.text)
            if (syn.type == Syn) {
                logs.logData("Device Interceptor : syn from ${message.message.author}")
                val ack = WifiDirectTaskMessage(type = Ack, content = "")
                wifiDirectCore.sendMessage(Json.encodeToString(ack))
            }
        } catch (_: Throwable) {
            // todo figure out why message wrong
        }
    }
}