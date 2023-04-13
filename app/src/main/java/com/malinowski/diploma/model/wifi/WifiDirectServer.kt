package com.malinowski.diploma.model.wifi

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

class WifiDirectServer : WifiDirectSocket() {

    init {
        try {
            launch { runServer() }
        } catch (e: Exception) {
            Log.e("RASPBERRY_SOCKET", e.message ?: "server error")
        }
    }

    private suspend fun runServer() = withContext(Dispatchers.IO) {
        val serverSocket = ServerSocket(PORT)
        onConnectionChanged(false)
        Log.i("RASPBERRY_SOCKET", "wait for client!!!")
        val client = serverSocket.accept()
        onConnectionChanged(true)
        Log.i("RASPBERRY_SOCKET", "client connect ${client.inetAddress.hostName}!!!")
        outputStream = client.getOutputStream()

        while (client.isConnected) {
            val text = BufferedReader(InputStreamReader(client.inputStream)).readText()
            onReceive(text)
            Log.i("RASPBERRY_MESSAGE", text)
        }
        onConnectionChanged(false)
        serverSocket.close()
    }
}