package com.example.edge_data.internal

import com.example.edge_entities.EdgeDevice
import retrofit2.Retrofit

private const val BASE_URL = "https://6761-146-66-165-41.ngrok-free.app/api/"

internal class EdgeDataRepository {
    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()

    private var service: EdgeDataService = retrofit.create(EdgeDataService::class.java)

    fun getOnlineDevices() : List<EdgeDevice> {
//        return service.getOnline().map {
//            EdgeDevice(it.deviceName)
//        }
        return listOf(
            EdgeDevice("Pixel 6"),
            EdgeDevice("Xiaomi Redmi Pro"),
        )
    }

}