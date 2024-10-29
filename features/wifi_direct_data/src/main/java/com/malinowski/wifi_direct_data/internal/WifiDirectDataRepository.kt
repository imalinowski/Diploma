package com.malinowski.wifi_direct_data.internal

import android.util.Log
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.api.WifiDirectEvents.MessageData
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessage
import com.malinowski.wifi_direct_data.internal.model.WifiDirectTaskMessageType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
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
    private val devicesInterceptor: WifiDirectDevicesInterceptor,
    mapper: WifiDirectEventsMapper
) {
    @OptIn(FlowPreview::class)
    val eventsFlow = wifiDirectCore.dataFlow
        .filterIsInstance<MessageData>()
        .debounce(100)
        .distinctUntilChanged(::areMessagesSame)
        .map {
            Log.i(
                "RASPBERRY_WD_DATA",
                "ON EACH message from wifi direct (${this.hashCode()} ${wifiDirectCore.hashCode()}) ${it.message}"
            )
            devicesInterceptor.interceptSyn(it)
            it
        }
        .map(mapper)
        .filterNotNull()

    private fun areMessagesSame(
        old: MessageData,
        new: MessageData,
    ): Boolean {
        val sameAuthors = old.message.author == new.message.author
        val sameText = old.message.text == new.message.text
        Log.i("RASPBERRY_WD_2", "DISTINCT are messages same ${sameAuthors && sameText}")
        return sameAuthors && sameText
    }

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
        wifiDirectCore.sendMessage(text)
    }
}