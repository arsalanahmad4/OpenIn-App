package com.example.openinapp.api

import com.example.openinapp.data.model.DashboardResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface OpeninApi {

    @GET("api/v1/dashboardNew")
    suspend fun dashboardApi(): Response<DashboardResponse>
}