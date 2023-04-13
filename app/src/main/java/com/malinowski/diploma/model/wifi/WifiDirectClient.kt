package com.malinowski.diploma.model.wifi

import android.util.Log
import com.malinowski.diploma.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.CoroutineContext

class WifiDirectClient(
    hostAddress: String,
    onReceiveMessage: (Message)->Unit
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private val socket = Socket()
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    init {
        launch(Dispatchers.IO) {
            socket.connect(InetSocketAddress(hostAddress, WifiDirectServer.PORT), 500)
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
            val text = BufferedReader(InputStreamReader(inputStream)).readLine()
            onReceiveMessage(Message(hostAddress, text))
            Log.i("RASPBERRY_MESSAGE", text)
        }

    }

    fun write(message: String) {
        outputStream?.write(message.toByteArray())
    }
}