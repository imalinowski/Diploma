package com.malinowski.wifi_direct_data.internal

import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.NewRemoteTask
import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeResult
import com.example.edge_entities.tasks.EdgeSubTaskBasic
import com.example.wifi_direct.api.DiscoverPeersResult.Error
import com.example.wifi_direct.api.DiscoverPeersResult.Peers
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.api.WifiDirectEvents
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WifiDirectDataRepository
@Inject constructor(
    private val wifiDirectCore: WifiDirectCore,
    mapper: WifiDirectEventsMapper
) {
    val eventsFlow = wifiDirectCore.dataFlow
        .filterIsInstance<WifiDirectEvents.MessageData>()
        .map(mapper)
        .onEach {
            if (it is NewRemoteTask) {
                tasksFromRemote.add(it)
            }
        }

    private val tasksFromRemote = mutableListOf<NewRemoteTask>()

    fun exit() {
        // todo how to exit from network ?
    }

    suspend fun getOnlineDevices(): List<EdgeDevice> {
        when (val result = wifiDirectCore.discoverPeers()) {
            is Error -> throw result.error
            is Peers -> return result.peers.map {
                EdgeDevice(it.deviceName, it.deviceAddress)
            }
        }
    }

    suspend fun executeByDevice(
        device: EdgeDevice,
        task: EdgeSubTaskBasic
    ) {
        val content = Json.encodeToString(task)
        val serializedTask = WifiDirectTaskMessage(
            type = WifiDirectTaskMessageType.Task, content = content
        )
        sendMessageTo(device.requireAddress(), serializedTask)
    }

    suspend fun sendToRemoteTaskResult(result: EdgeResult) {
        val author = tasksFromRemote.find { it.task.id == result.taskId }?.author
        if (author == null) {
            throw IllegalStateException("no task with if ${result.taskId}")
        }
        val content = Json.encodeToString(result)
        val serializedTask = WifiDirectTaskMessage(
            type = WifiDirectTaskMessageType.Result, content = content
        )
        sendMessageTo(author, serializedTask)
    }

    private suspend fun sendMessageTo(
        address: String,
        message: WifiDirectTaskMessage
    ) {
        val connected = wifiDirectCore.connect(address)
        if (connected.not()) {
            val content = Json.encodeToString(message)
            wifiDirectCore.sendMessage(content)
        } else {
            throw IllegalStateException("can't connect to $address")
        }
    }
}