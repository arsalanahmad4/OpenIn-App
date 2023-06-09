package com.example.openinapp.data

import com.example.openinapp.api.RetrofitInstance

class DashboardRepository {

    suspend fun getApiResponse(authToken:String) =
        RetrofitInstance.api.dashboardApi(authToken)
}