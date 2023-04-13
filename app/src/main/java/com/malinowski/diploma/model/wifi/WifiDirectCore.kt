package com.malinowski.diploma.model.wifi

import android.Manifest
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import kotlinx.coroutines.flow.Flow

internal val WIFI_CORE_PERMISSIONS by lazy {
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.INTERNET,
    )
}

//permission for Android 13
internal val WIFI_CORE_PERMISSIONS_13 by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
    } else arrayOf()
}

interface WifiDirectCore {

    val logFlow: Flow<WifiDirectData?>

    fun registerReceiver()

    fun unRegisterReceiver()

    suspend fun discoverPeers(): WifiDirectResult<List<WifiP2pDevice>>

    suspend fun connect(address: String): WifiDirectResult<Boolean>

    suspend fun connectCancel(address: String): WifiDirectResult<Boolean>

    suspend fun sendMessage(message: String)
}