package com.malinowski.wifi_direct_data.internal

import android.util.Log
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.api.WifiDirectEvents
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessage
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessageType
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WifiDirectDataRepository
@Inject constructor(
    private val wifiDirectCore: WifiDirectCore,
    private val devicesInterceptor: WifiDirectDevicesInterceptor,
    mapper: WifiDirectEventsMapper
) {
    val eventsFlow = wifiDirectCore.dataFlow
        .filterIsInstance<WifiDirectEvents.MessageData>()
        .onEach { devicesInterceptor.interceptSyn(it) }
        .map(mapper)
        .filterNotNull()

    fun exit() {
        // todo how to exit from network ?
    }

    suspend fun getOnlineDevices(): List<EdgeDevice> {
        return devicesInterceptor.getOnlineDevices()
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
        Log.i("RASPBERRY", "send to remote task result $text")
        wifiDirectCore.sendMessage(text)
    }
}