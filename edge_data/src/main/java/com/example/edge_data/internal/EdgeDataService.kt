package com.example.edge_data.internal

import com.example.edge_data.internal.models.EnterExitRequest
import com.example.edge_data.internal.models.ExecuteRequest
import com.example.edge_data.internal.models.NetworkDevice
import com.example.edge_data.internal.models.NetworkTask
import com.example.edge_data.internal.models.NetworkTaskResult
import com.example.edge_data.internal.models.PostTaskRequest
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface EdgeDataService {

    @GET("/api/getonline")
    suspend fun getOnline(): List<NetworkDevice>

    @POST("/api/enter")
    suspend fun enter(
        @Body request: EnterExitRequest
    ): NetworkDevice

    @POST("/api/exit")
    suspend fun exit(
        @Body request: EnterExitRequest
    ): NetworkDevice

    @POST("/api/execute")
    suspend fun execute(
        @Body request: ExecuteRequest
    ): NetworkDevice

    @GET("/api/gettask")
    suspend fun getTask(
        @Query("device_name") deviceName: String
    ): List<NetworkTask>

    @POST("/api/posttask")
    suspend fun posttask(
        @Body request: PostTaskRequest
    ): NetworkTask

    @POST("/api/result")
    suspend fun getResult(
        @Query("task_id") taskId: Int
    ): NetworkTaskResult
}