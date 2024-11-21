package com.example.wifi_direct.internal.wifi

import android.util.Log
import com.example.entities.getTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.coroutines.CoroutineContext

private const val BUFFER_SIZE = 1024
private const val DELAY_FOR_READING = 100L
private const val LIMIT_FOR_REPEATS_READING = 50
private const val END_OF_MESSAGE = "<EOFin+E+C9Vj3uxS5IxDZtZ6J9Alo=>"

abstract class WifiDirectSocket(
    val hostAddress: String
) : CoroutineScope {

    companion object {
        const val RESTART_LIMIT = 2
        const val PORT: Int = 9999
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    var onReceive: (String) -> Unit = {}

    var log: (String) -> Unit = {}

    var onConnectionChanged: (Boolean) -> Unit = {}

    protected lateinit var socket: Socket

    private lateinit var outputStream: OutputStream

    private var connected: Boolean = false
        set(value) {
            field = value
            onConnectionChanged(value)
        }

    private var restartCount = 0

    protected abstract fun initConnection()

    protected suspend fun start() = withContext(Dispatchers.IO) {
        connected = true
        outputStream = socket.getOutputStream()
        val inputStream = socket.getInputStream()
        val buffer = ByteArray(BUFFER_SIZE)
        Log.i("RASPBERRY", "socket : $socket launched")
        try {
            while (connected) {
                val text = readAllBytes(inputStream, buffer)
                if (text.isNotEmpty()) {
                    Log.i("RASPBERRY_MESSAGE", "${getTime()} received : $text")
                    onReceive(text)
                    log("RASPBERRY_MESSAGE : $text")
                }
            }
        } catch (e: Exception) {
            shutDown()
        }
    }

    private suspend fun readAllBytes(inputStream: InputStream, buffer: ByteArray): String {
        // ожидание ответа конец если ответ не приходит слишком долго
        var waitCounter = 0
        while (inputStream.available() == 0) {
            delay(DELAY_FOR_READING)
            waitCounter += 1
            if (waitCounter == LIMIT_FOR_REPEATS_READING) {
                return ""
            }
        }
        var text = ""
        while (inputStream.available() > 0) {
            val len = inputStream.read(buffer)
            Log.i("RASPBERRY_MESSAGE", "received message len $len")
            text += String(buffer, 0, len)
        }
        // конец рекурсии если конец сообшения
        return if (text.endsWith(END_OF_MESSAGE)) {
            text.take(text.length - END_OF_MESSAGE.length)
        } else {
            text + readAllBytes(inputStream, buffer)
        }
    }

    suspend fun write(message: String) = withContext(Dispatchers.IO) {
        val packedMessage = message + END_OF_MESSAGE
        outputStream.write(packedMessage.toByteArray())
        log("RASPBERRY_MESSAGE : send message $message")
        Log.i("RASPBERRY_MESSAGE", "${getTime()} : send message $message")
    }

    open fun shutDown(restart: Boolean = true, error: Exception? = null) {
        log("RASPBERRY_SHUT_DOWN restart > $restart, error >${error.toString()}")
        Log.e("RASPBERRY_SHUT_DOWN", "restart > $restart, error >${error.toString()}")
        socket.close()
        connected = false
        if (restart && restartCount < RESTART_LIMIT) {
            Log.i("RASPBERRY", "socket restarted")
            initConnection()
            restartCount += 1
        } else {
            restartCount = RESTART_LIMIT
        }
    }
}