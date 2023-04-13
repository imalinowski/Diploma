package com.malinowski.diploma.model.wifi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext

abstract class WifiDirectSocket : CoroutineScope {

    var onReceive: (String) -> Unit = {}

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    var outputStream: OutputStream? = null

    suspend fun write(message: String) = withContext(Dispatchers.IO) {
        outputStream?.write(message.toByteArray())
    }

    companion object {
        const val PORT: Int = 8080
    }
}