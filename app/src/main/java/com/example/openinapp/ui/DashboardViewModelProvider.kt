package com.example.openinapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.openinapp.data.DashboardRepository

class DashboardViewModelProvider(private val application: Application):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                dashboardRepository = DashboardRepository(
                ), application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}