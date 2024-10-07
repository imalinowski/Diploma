package com.example.wifi_direct.api

import android.Manifest
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Build
import kotlinx.coroutines.flow.Flow

val WIFI_CORE_PERMISSIONS by lazy {
    listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.INTERNET,
    )
}

//permission for Android 13
val WIFI_CORE_PERMISSIONS_13 by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.NEARBY_WIFI_DEVICES)
    } else listOf()
}

interface WifiDirectCore {

    val dataFlow: Flow<WifiDirectEvents?>

    fun registerReceiver()

    fun unRegisterReceiver()

    fun getConnectionInfo(): WifiP2pInfo

    suspend fun discoverPeers(): DiscoverPeersResult

    suspend fun connect(address: String): Boolean

    suspend fun connectCancel(address: String): Boolean

    suspend fun sendMessage(message: String)
}