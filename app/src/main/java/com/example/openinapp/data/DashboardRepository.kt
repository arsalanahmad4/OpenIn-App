package com.example.openinapp.data

import com.example.openinapp.api.RetrofitInstance

class DashboardRepository {

    suspend fun getDashboardApiResponse() =
        RetrofitInstance.api.dashboardApi()
}