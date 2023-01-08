package com.malinowski.diploma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.wifi.WifiDirectCore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WifiDirectState(
    val logText: String = ""
)

class WifiDirectViewModel @Inject constructor(
    private val wifiDirectCore: WifiDirectCore
) : ViewModel() {

    private val _uiState = MutableStateFlow(WifiDirectState())
    private val _actions: MutableStateFlow<WifiDirectActions?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()
    val actions = _actions.asStateFlow()

    init {
        viewModelScope.launch {
            wifiDirectCore.stateFlow.collectLatest {
                appendText(it)
            }
        }
    }

    fun appendText(text: String) {
        _uiState.value = _uiState.value.let { state ->
            state.copy(
                logText = state.logText + "\n ${System.currentTimeMillis()}: $text"
            )
        }
    }

    fun clearText() {
        _uiState.value = _uiState.value.copy(logText = "")
    }

    fun searchForDevices() {
        wifiDirectCore.discoverPeers()
    }

    fun registerReceiver() {
        wifiDirectCore.registerReceiver()
    }

    fun unregisterReceiver() {
        wifiDirectCore.unregisterReceiver()
    }
}