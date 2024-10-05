package com.malinowski.chat.internal.model.wifi

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ServerSocket

class WifiDirectServer(
    hostAddress: String
) : WifiDirectSocket(hostAddress) {

    private lateinit var serverSocket: ServerSocket

    init {
        initConnection()
    }

    override fun initConnection() {
        launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(PORT)
                socket = serverSocket.accept()
                start()
            } catch (e: Exception) {
                Log.e("RASPBERRY_SERVER", e.message ?: "server error")
            }
        }
    }

    override fun shutDown(restart: Boolean, error: Exception?) {
        super.shutDown(restart, error)
        serverSocket.close()
    }
}