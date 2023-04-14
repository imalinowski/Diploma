package com.malinowski.diploma.model.wifi

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.Socket
import kotlin.coroutines.CoroutineContext

abstract class WifiDirectSocket : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    var onReceive: (String) -> Unit = {}

    var onConnectionChanged: (Boolean) -> Unit = {}

    protected lateinit var socket: Socket

    private lateinit var outputStream: OutputStream

    private var connected: Boolean = false
        set(value) {
            field = value
            onConnectionChanged(value)
        }

    protected suspend fun start() = withContext(Dispatchers.IO) {
        connected = true
        outputStream = socket.getOutputStream()
        val inputStream = socket.getInputStream()
        val buffer = ByteArray(1024)
        Log.i("RASPBERRY", "socket : $socket launched")
        while (connected) {
            try {
                val len = inputStream.read(buffer)
                Log.i("RASPBERRY_MESSAGE", "received message len $len")
                if (len == 0) continue
                val text = String(buffer, 0, len)
                onReceive(text)
                Log.i("RASPBERRY_MESSAGE", text)
            } catch (e: Exception) {
                shutDown()
            }
        }
    }

    suspend fun write(message: String) = withContext(Dispatchers.IO) {
        outputStream.write(message.toByteArray())
        Log.i("RASPBERRY", "send message $message to ${socket.inetAddress.hostName}")
    }

    open fun shutDown(e: Exception? = null) {
        Log.e("RASPBERRY_SHUT_DOWN", e.toString())
        socket.close()
        connected = false
    }

    companion object {
        const val PORT: Int = 8080
    }
}