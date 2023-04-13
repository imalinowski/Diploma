package com.malinowski.diploma.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malinowski.diploma.model.Message
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.WifiDirectPeer
import com.malinowski.diploma.model.wifi.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WifiDirectState(
    val logText: String = "",
    val peers: List<WifiDirectPeer> = emptyList(),
    val permissionsGranted: Boolean = false
)

class WifiDirectViewModel @Inject constructor(
    private val wifiDirectCore: WifiDirectCore
) : ViewModel() {

    private val _state = MutableStateFlow(WifiDirectState())
    private val _actions: MutableStateFlow<WifiDirectActions?> = MutableStateFlow(null)
    val state = _state.asStateFlow()
    val actions = _actions.asStateFlow()

    init {
        wifiDirectCore.registerReceiver()
        viewModelScope.launch {
            wifiDirectCore.logFlow.collectLatest {
                when (it) {
                    is WifiDirectData.LogData -> log(it.log)
                    is WifiDirectData.MessageData -> TODO()
                    null -> {}
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wifiDirectCore.unRegisterReceiver()
    }

    private fun log(text: String) {
        _state.value = _state.value.let { state ->
            state.copy(
                logText = "${state.logText}\n${System.currentTimeMillis()}: $text"
            )
        }
    }

    fun getMessages(peer: WifiDirectPeer): List<Message> {
        val sampleName = "Vasya"
        val loremIpsum =
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
        return listOf(
            Message(author = peer.name, text = loremIpsum),
            Message(author = sampleName, text = loremIpsum),
            Message(author = peer.name, text = loremIpsum),
            Message(author = sampleName, text = loremIpsum),
            Message(author = peer.name, text = loremIpsum),
            Message(author = sampleName, text = loremIpsum),
            Message(author = sampleName, text = loremIpsum),
            Message(author = peer.name, text = loremIpsum),
            Message(author = sampleName, text = loremIpsum),
            Message(author = peer.name, text = loremIpsum),
            Message(author = sampleName, text = loremIpsum),
        )
    }

    fun clearLog() {
        _state.value = _state.value.copy(logText = "")
    }

    fun searchForDevices() {
        viewModelScope.launch {
            when (val result = wifiDirectCore.discoverPeers()) {
                is WifiDirectResult.Success -> _state.value =
                    _state.value.copy(peers = result.data.map {
                        WifiDirectPeer(it.deviceName, it.deviceAddress)
                    })
                is WifiDirectResult.Error ->
                    _actions.value = WifiDirectActions.ShowAlertDialog(
                        title = result.error::class.java.name,
                        text = result.error.message ?: "error"
                    )
            }
        }
    }

    fun connectDevice(peer: WifiDirectPeer) {
        viewModelScope.launch {
            when (val result = wifiDirectCore.connect(peer.address)) {
                is WifiDirectResult.Success ->
                    _actions.value = WifiDirectActions.OpenChat(peer)
                is WifiDirectResult.Error ->
                    _actions.value = WifiDirectActions.ShowToast(
                        result.error.message ?: "Connect Failed"
                    )
            }
        }
    }

    fun connectCancel(address: String) {
        viewModelScope.launch {
            when (val result = wifiDirectCore.connectCancel(address)) {
                is WifiDirectResult.Success -> {}
                is WifiDirectResult.Error ->
                    _actions.value = WifiDirectActions.ShowToast(
                        result.error.message ?: "Error Device DisConnect"
                    )
            }
        }
    }

    fun sendMessage(message: String){
        viewModelScope.launch {
            wifiDirectCore.sendMessage(message)
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