package com.malinowski.wifi_direct_data.internal

import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.PeersChanged
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.api.WifiDirectEvents
import com.example.wifi_direct.api.WifiDirectEvents.MessageData
import com.example.wifi_direct.api.WifiDirectEvents.PeersChangedAction
import com.malinowski.base_logs.api.Logs
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessage
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessageType
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiDirectDataRepository
@Inject constructor(
    private val wifiDirectCore: WifiDirectCore,
    private val messageInterceptor: WifiDirectDevicesInterceptor,
    private val messageMapper: WifiDirectMessageMapper,
    private val logs: Logs,
) {
    val eventsFlow = wifiDirectCore.dataFlow
        .map(::mapToEdgeEvents)
        .catch {
            emit(EdgeDataEvent.Error(it))
        }
        .filterNotNull()

    private suspend fun mapToEdgeEvents(event: WifiDirectEvents?): EdgeDataEvent? {
        return when (event) {
            is WifiDirectEvents.LogData -> {
                logs.logData(event.log)
                null
            }

            PeersChangedAction -> {
                PeersChanged(messageInterceptor.getOnlineDevices())
            }

            is MessageData -> {
                messageInterceptor.tryInterceptSyn(event)
                messageMapper(event)
            }

            else -> null
        }
    }

    suspend fun exit() {
        wifiDirectCore.connectCancel()
    }

    suspend fun getOnlineDevices(): List<EdgeDevice> {
        return messageInterceptor.getOnlineDevices()
    }

    suspend fun executeByDevice(
        device: EdgeDevice,
        task: EdgeSubTaskBasic
    ) {
        val content = Json.encodeToString(task.params)
        val serializedTask = WifiDirectTaskMessage(
            type = WifiDirectTaskMessageType.Task(task.id),
            content = content
        )
        sendMessageTo(device.requireAddress(), serializedTask)
    }

    private suspend fun sendMessageTo(
        address: String,
        message: WifiDirectTaskMessage
    ) {
        if (wifiDirectCore.connect(address)) {
            val content = Json.encodeToString(message)
            wifiDirectCore.sendMessage(content)
        } else {
            throw IllegalStateException("can't connect to $address")
        }
    }

    suspend fun sendToRemoteTaskResult(result: EdgeResult) {
        val content = Json.encodeToString(result)
        val serializedTask = WifiDirectTaskMessage(
            type = WifiDirectTaskMessageType.Result,
            content = content
        )
        val text = Json.encodeToString(serializedTask)
        wifiDirectCore.sendMessage(text)
    }
}