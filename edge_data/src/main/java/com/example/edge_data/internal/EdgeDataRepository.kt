package com.example.edge_data.internal

import com.example.edge_data.internal.mappers.NetworkToEdgeTaskMapper
import com.example.edge_data.internal.models.EnterExitRequest
import com.example.edge_data.internal.models.ExecuteRequest
import com.example.edge_data.internal.models.NetworkTask
import com.example.edge_data.internal.models.PostTaskRequest
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.NewRemoteTask
import com.example.edge_domain.api.dependecies.data.EdgeDataEvent.SubTaskCompleted
import com.example.edge_entities.EdgeDevice
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.CoroutineContext

private const val BASE_URL = "https://7aac-146-66-165-41.ngrok-free.app/api/"

internal class EdgeDataRepository(
    private val deviceName: String,
    private val mapper: NetworkToEdgeTaskMapper
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    private val contentType = MediaType.get("application/json")
    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(unSafeOkHttpClient().build())
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()

    private var service: EdgeDataService =
        retrofit.create(EdgeDataService::class.java)

    private val localSubTasks: MutableList<NetworkTask> = mutableListOf()

    val eventsFlow = MutableSharedFlow<EdgeDataEvent>()

    init {
        launch {
            enter()
        }
        launch {
            while (true) {
                checkForNewTasks()
                checkForResult()
                delay(10000)
            }
        }
    }

    private suspend fun enter() {
        println(
            "RASPBERRY ${
                service.enter(EnterExitRequest(deviceName))
            }"
        )
    }

    private suspend fun checkForNewTasks() {
        val newRemoteTasks = service.getTask(deviceName)
        newRemoteTasks.forEach { task ->
            eventsFlow.emit(
                NewRemoteTask(mapper.map(task))
            )
        }
    }

    private suspend fun checkForResult() {
        localSubTasks.forEach { task ->
            try {
                val response = service.getResult(task.id)
                eventsFlow.emit(
                    SubTaskCompleted(
                        taskId = task.id,
                        result = Json.decodeFromString(response.taskResult)
                    )
                )
            } catch (e: Throwable) {
            }
        }
    }

    suspend fun getOnlineDevices(): List<EdgeDevice> {
        return service.getOnline().map {
            EdgeDevice(it.deviceName)
        }
    }

    suspend fun executeByDevice(deviceName: String, task: NetworkTask) {
        localSubTasks.add(task)
        val request = ExecuteRequest(
            deviceName = deviceName,
            task = task
        )
        service.execute(request)
    }

    suspend fun sendToRemoteTaskResult(
        taskId: Int,
        result: String
    ) {
        val request = PostTaskRequest(
            id = taskId,
            taskResult = result,
            deviceName = ""
        )
        service.posttask(request)
    }

    fun unSafeOkHttpClient() : OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts:  Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?){}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate>  = arrayOf()
            })

            // Install the all-trusting trust manager
            val  sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() &&  trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(sslSocketFactory, trustAllCerts.first() as X509TrustManager)
                okHttpClient.hostnameVerifier { _,_ -> true }
            }

            return okHttpClient
        } catch (e: Exception) {
            return okHttpClient
        }
    }
}