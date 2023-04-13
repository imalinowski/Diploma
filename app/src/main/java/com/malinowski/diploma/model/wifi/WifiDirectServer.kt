package com.malinowski.diploma.model.wifi

import android.util.Log
import com.malinowski.diploma.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import kotlin.coroutines.CoroutineContext

class WifiDirectServer(
    onReceiveMessage: (Message) -> Unit
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private var outputStream: OutputStream? = null

    init {
        launch(Dispatchers.IO) {
            val serverSocket = ServerSocket(PORT)
            val client = serverSocket.accept()
            outputStream = client.getOutputStream()
            val text = BufferedReader(InputStreamReader(client.inputStream)).readLine()
            onReceiveMessage(Message(client.inetAddress.hostAddress ?: "", text))
            Log.i("RASPBERRY_MESSAGE", text)
            serverSocket.close()
        }
    }

    fun write(message: String) {
        outputStream?.write(message.toByteArray())
    }

    companion object {
        const val PORT: Int = 8080
    }
}