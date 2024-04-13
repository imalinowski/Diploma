package com.malinowski.diploma.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.internal.ext.getTime
import com.malinowski.diploma.model.Message
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.WifiDirectPeer
import com.malinowski.diploma.model.WifiDirectUiState
import com.malinowski.diploma.model.wifi.WIFI_CORE_PERMISSIONS
import com.malinowski.diploma.model.wifi.WIFI_CORE_PERMISSIONS_13
import com.malinowski.diploma.model.wifi.WifiDirectCore
import com.malinowski.diploma.model.wifi.WifiDirectData
import com.malinowski.diploma.model.wifi.WifiDirectResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class WifiDirectViewModel @Inject constructor(
    private val wifiDirectCore: WifiDirectCore
) : ViewModel() {

    private val _state = MutableStateFlow(WifiDirectUiState())
    private val _actions: MutableStateFlow<WifiDirectActions?> = MutableStateFlow(null)
    val state = _state.asStateFlow()
    val actions = _actions.asStateFlow()

    init {
        wifiDirectCore.registerReceiver()
        viewModelScope.launch {
            wifiDirectCore.dataFlow.collectLatest { data ->
                when (data) {
                    is WifiDirectData.LogData -> log(data.log)
                    is WifiDirectData.MessageData -> addMessage(data.message)
                    is WifiDirectData.WifiConnectionChanged -> _state.value =
                        _state.value.copy(wifiConnectionInfo = data.info)

                    null -> {}
                    is WifiDirectData.SocketConnectionChanged -> _state.value =
                        _state.value.copy(chatConnectionInfo = data.connected)
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
        _actions.value = WifiDirectActions.ShowAlertDialog(
            title = error::class.java.name,
            text = error.message ?: "error"
        )
    }

    fun clearLog() {
        saveLogs()
        _state.value = _state.value.copy(logText = "")
    }

    fun searchForDevices() {
        viewModelScope.launch {
            when (val result = wifiDirectCore.discoverPeers()) {
                is WifiDirectResult.Peers -> _state.value =
                    _state.value.copy(peers = result.peer.map {
                        WifiDirectPeer(it.deviceName, it.deviceAddress)
                    })

                is WifiDirectResult.Error -> showErrorAlertDialog(result.error)
                else -> {}
            }
        }
    }

    fun connectDevice(peer: WifiDirectPeer) {
        viewModelScope.launch {
            if (wifiDirectCore.connect(peer.address)) {
                _actions.value = WifiDirectActions.OpenChat(peer)
            } else {
                _actions.value = WifiDirectActions.ShowToast("Connect Failed")
            }
        }
    }

    fun saveLogs() {
        _actions.value = WifiDirectActions.SaveLogs(
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
            _actions.value = WifiDirectActions.RequestPermissions(permission)
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