package com.example.openinapp.ui.dashboard.links

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.example.openinapp.OpeninApplication
import com.example.openinapp.data.DashboardRepository
import com.example.openinapp.data.model.DashboardResponse
import com.example.openinapp.util.MutableAppropriateLiveData
import com.example.openinapp.util.Resource
import com.example.openinapp.util.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class LinksViewModel(private val dashboardRepository: DashboardRepository, app: Application):AndroidViewModel(app) {

    private val _dashboardResponse = MutableAppropriateLiveData<Resource<DashboardResponse>>()
    val dashboardResponseResult: LiveData<Resource<DashboardResponse>> = _dashboardResponse

    fun getDashboardApiData() {
        viewModelScope.launch {
            safeGetDashboardResponse()
        }
    }
    private suspend fun safeGetDashboardResponse() {
        _dashboardResponse.postValue(Resource.Loading())
        try {
            val connectivityManager = getApplication<OpeninApplication>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            if (hasInternetConnection(connectivityManager)) {
                val response = dashboardRepository.getDashboardApiResponse()
                _dashboardResponse.postValue(handleDashboardApiResponse(response))
            } else {
                _dashboardResponse.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _dashboardResponse.postValue(Resource.Error("Network Failure"))
                else -> _dashboardResponse.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleDashboardApiResponse(response: Response<DashboardResponse>): Resource<DashboardResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}