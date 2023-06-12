package com.example.openinapp.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.openinapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashboardActivity : AppCompatActivity() {
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bttm_nav)
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.dashboard_nav_graph)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

}