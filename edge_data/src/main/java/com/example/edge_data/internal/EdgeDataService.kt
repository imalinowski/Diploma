package com.example.edge_data.internal

import com.example.edge_data.internal.models.NetworkDevice
import retrofit2.http.GET

internal interface EdgeDataService {

    @GET("/api/getonline")
    fun getOnline(): List<NetworkDevice>
}