package com.malinowski.diploma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.WifiDirectPeer
import com.malinowski.diploma.model.wifi.WifiDirectCoreOld
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WifiDirectUIState(
    val logText: String = "",
    val peers: List<WifiDirectPeer> = emptyList()
)

class WifiDirectViewModel @Inject constructor(
    private val wifiDirectCoreOld: WifiDirectCoreOld
) : ViewModel() {

    private val _uiState = MutableStateFlow(WifiDirectUIState())
    private val _actions: MutableStateFlow<WifiDirectActions?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()
    val actions = _actions.asStateFlow()

    init {
        wifiDirectCoreOld.registerReceiver()
        viewModelScope.launch {
            launch {
                wifiDirectCoreOld.stateFlow.collectLatest {
                    appendText(it)
                }
            }
            wifiDirectCoreOld.peerFlow.collectLatest { peers ->
                _uiState.value = _uiState.value.copy(
                    peers = peers.map { WifiDirectPeer(it.deviceName) }
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wifiDirectCoreOld.unregisterReceiver()
    }

    fun appendText(text: String) {
        _uiState.value = _uiState.value.let { state ->
            state.copy(
                logText = state.logText + "\n ${System.currentTimeMillis()}: $text"
            )
        }
    }

    fun requestPermissions(permission: Array<String>) {
        _actions.value = WifiDirectActions.RequestPermissions(permission)
    }

    fun clearText() {
        _uiState.value = _uiState.value.copy(logText = "")
    }

    fun searchForDevices() {
        wifiDirectCoreOld.discoverPeers()
    }
}