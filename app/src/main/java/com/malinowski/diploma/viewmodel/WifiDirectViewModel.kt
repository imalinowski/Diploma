package com.malinowski.diploma.viewmodel

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
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
    private val wifiDirectCore: WifiDirectCore,
    private var context: Context // todo RemoveIt
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

    private fun appendText(text: String) {
        _uiState.value = _uiState.value.let { state ->
            state.copy(
                logText = state.logText + "\n ${System.currentTimeMillis()}: $text"
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun searchForDevices() {
        if (!checkPermission(ACCESS_FINE_LOCATION)) {
            _actions.value = WifiDirectActions.RequestPermissions(
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_WIFI_STATE,
                    CHANGE_WIFI_STATE,
                    INTERNET,
                )
            )
            appendText("Permission Denied")
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !checkPermission(NEARBY_WIFI_DEVICES)
        ) {
            _actions.value = WifiDirectActions.RequestPermissions(
                arrayOf(NEARBY_WIFI_DEVICES)
            )
            appendText("Permission Denied")
            return
        }
        wifiDirectCore.discoverPeers()

    }

    fun registerReceiver() {
        wifiDirectCore.registerReceiver()
    }

    fun unregisterReceiver() {
        wifiDirectCore.unregisterReceiver()
    }
}