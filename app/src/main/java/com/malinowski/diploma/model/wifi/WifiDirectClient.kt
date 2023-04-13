package com.malinowski.diploma.model.wifi

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket

class WifiDirectClient(
    private val hostAddress: String,
) : WifiDirectSocket() {
    init {
        try {
            launch { runClient() }
        } catch (e: Exception) {
            Log.e("RASPBERRY_SOCKET", e.message ?: "client error")
        }
    }

    private suspend fun runClient() = withContext(Dispatchers.IO) {
        val socket = Socket()
        Log.i("RASPBERRY_SOCKET", "client trying to connect $hostAddress")
        socket.connect(InetSocketAddress(hostAddress, PORT))
        Log.i("RASPBERRY_SOCKET", "client connect $hostAddress")

        val inputStream = socket.getInputStream()
        outputStream = socket.getOutputStream()

        while (socket.isConnected) {
            val text = BufferedReader(InputStreamReader(inputStream)).readText()
            onReceive(text)
            Log.i("RASPBERRY_MESSAGE", text)
        }
    }
}