package com.malinowski.diploma.model.wifi

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket
import java.util.*
import kotlin.coroutines.CoroutineContext

abstract class WifiDirectSocket : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    var onReceive: (String) -> Unit = {}

    var onConnectionChanged: (Boolean) -> Unit = {}

    protected lateinit var socket: Socket

    private var connected: Boolean = false
        set(value) {
            field = value
            onConnectionChanged(value)
        }

    private var outputStream: OutputStream? = null

    protected fun start(){
        connected = true
        outputStream = socket.getOutputStream()
        val reader = Scanner(socket.getInputStream())
        while (connected) {
            try {
                val text = reader.nextLine()
                if (text.isNotEmpty()) onReceive(text)
                Log.i("RASPBERRY_MESSAGE", text)
            } catch (e: Exception) {
                shutDown()
            }
        }
    }

    suspend fun write(message: String) = withContext(Dispatchers.IO) {
        outputStream?.write(message.toByteArray())
            ?: throw IllegalStateException("outputStream is null")
    }

    protected open fun shutDown(){
        socket.close()
        connected = false
    }

    companion object {
        const val PORT: Int = 8080
    }
}