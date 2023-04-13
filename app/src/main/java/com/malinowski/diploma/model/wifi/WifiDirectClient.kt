package com.malinowski.diploma.model.wifi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket

class WifiDirectClient(
    private val hostAddress: String,
) : WifiDirectSocket() {
    init {
        launch(Dispatchers.IO) {
            socket = Socket()
            socket.connect(InetSocketAddress(hostAddress, PORT))
            start()
        }
    }
}