package com.example.wifi_direct.internal.wifi.sockets

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket

class WifiDirectClient(
    hostAddress: String,
) : WifiDirectSocket(hostAddress) {
    init {
        initConnection()
    }

    override fun initConnection() {
        launch(Dispatchers.IO) {
            try {
                socket = Socket()
                socket.connect(InetSocketAddress(hostAddress, PORT))
                start()
            } catch (e: Exception) {
                Log.e("RASPBERRY_CLIENT", e.toString())
            }
        }
    }
}