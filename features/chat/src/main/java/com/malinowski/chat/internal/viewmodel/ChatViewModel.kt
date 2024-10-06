package com.malinowski.chat.internal.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wifi_direct.api.DiscoverPeersResult
import com.example.wifi_direct.api.Message
import com.example.wifi_direct.api.WIFI_CORE_PERMISSIONS
import com.example.wifi_direct.api.WIFI_CORE_PERMISSIONS_13
import com.example.wifi_direct.api.WifiDirectCore
import com.example.wifi_direct.api.WifiDirectData.LogData
import com.example.wifi_direct.api.WifiDirectData.MessageData
import com.example.wifi_direct.api.WifiDirectData.SocketConnectionChanged
import com.example.wifi_direct.api.WifiDirectData.WifiConnectionChanged
import com.example.wifi_direct.internal.ext.getTime
import com.malinowski.chat.internal.model.ChatActions
import com.malinowski.chat.internal.model.ChatPeer
import com.malinowski.chat.internal.model.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// todo rewrite to common arch
class ChatViewModel @Inject constructor(
    private val wifiDirectCore: WifiDirectCore
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    private val _actions: MutableStateFlow<ChatActions?> = MutableStateFlow(null)
    val state = _state.asStateFlow()
    val actions = _actions.asStateFlow()

    init {
        wifiDirectCore.registerReceiver()
        viewModelScope.launch {
            wifiDirectCore.dataFlow.collectLatest { data ->
                when (data) {
                    is LogData -> log(data.log)
                    is MessageData -> addMessage(data.message)
                    is WifiConnectionChanged -> {
                        _state.value = _state.value.copy(wifiConnectionInfo = data.info)
                    }
                    is SocketConnectionChanged -> {
                        _state.value = _state.value.copy(chatConnectionInfo = data.connected)
                    }
                    null -> {}
                }
            }
        }
    }

    override fun onCleared() {
        saveLogs()
        super.onCleared()
        wifiDirectCore.unRegisterReceiver()
    }

    private fun log(text: String) {
        _state.value = _state.value.let { state ->
            state.copy(
                logText = "${state.logText}\n${getTime()}: $text"
            )
        }
    }

    private fun addMessage(message: Message) {
        _state.value = _state.value.let { state ->
            state.copy(messages = state.messages + listOf(message))
        }
    }

    private fun showErrorAlertDialog(error: Throwable) {
        _actions.value = ChatActions.ShowAlertDialog(
            title = error::class.java.name,
            text = error.message ?: "error"
        )
    }

    fun clearLog() {
        _state.value = _state.value.copy(logText = "")
    }

    fun searchForDevices() {
        viewModelScope.launch {
            when (val result = wifiDirectCore.discoverPeers()) {
                is DiscoverPeersResult.Peers -> _state.value =
                    _state.value.copy(peers = result.peers.map {
                        ChatPeer(it.deviceName, it.deviceAddress)
                    })

                is DiscoverPeersResult.Error -> showErrorAlertDialog(result.error)
                else -> {}
            }
        }
    }

    fun connectDevice(peer: ChatPeer) {
        viewModelScope.launch {
            if (wifiDirectCore.connect(peer.address)) {
                _actions.value = ChatActions.OpenChat(peer)
            } else {
                _actions.value = ChatActions.ShowToast("Connect Failed")
            }
        }
    }

    fun saveLogs() {
        _actions.value = ChatActions.SaveLogs(
            filename = "DIPLOMA_EXPERIMENT_${getTime()}",
            text = _state.value.logText
        )
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                wifiDirectCore.sendMessage(message)
                addMessage(Message(text = message, fromRemote = false, time = getTime("hh:mm:ss.SSS")))
            } catch (e: Exception) {
                showErrorAlertDialog(e)
            }
        }
    }

    fun checkPermissions(context: Context): Boolean {

        fun checkPermission(permission: String): Boolean {
            return ActivityCompat.checkSelfPermission(
                context, permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermissions(permission: Array<String>) {
            _actions.value = ChatActions.RequestPermissions(permission)
        }

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(WIFI_CORE_PERMISSIONS)
            log("Denied > ${Manifest.permission.ACCESS_FINE_LOCATION}")
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !checkPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
        ) {
            log("Denied > ${Manifest.permission.NEARBY_WIFI_DEVICES}")
            requestPermissions(WIFI_CORE_PERMISSIONS_13)
            return false
        }

        return true
    }
}