package com.malinowski.diploma.model.wifi

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

class WifiDirectServer : WifiDirectSocket() {
    init {
        launch(Dispatchers.IO) {
            val serverSocket = ServerSocket(PORT)
            Log.i("RASPBERRY_SOCKET", "wait for client!!!")
            val client = serverSocket.accept()
            Log.i("RASPBERRY_SOCKET", "clinet connect ${client.inetAddress.hostName}!!!")
            outputStream = client.getOutputStream()

            while (client.isConnected) {
                val text = BufferedReader(InputStreamReader(client.inputStream)).readText()
                onReceive(text)
                Log.i("RASPBERRY_MESSAGE", text)
            }

            serverSocket.close()
        }
    }
}