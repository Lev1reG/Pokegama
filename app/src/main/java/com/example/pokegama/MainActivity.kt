package com.example.pokegama

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pokegama.databinding.ActivityMainBinding
import com.example.pokegama.ui.splashScreen.SplashViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply{
            setKeepOnScreenCondition{
                !viewModel.isReady.value
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        supportActionBar?.hide()
        navView.setupWithNavController(navController)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_add -> {
                    navController.navigate(R.id.navigation_add)
                    true
                }
                R.id.navigation_about -> {
                    navController.navigate(R.id.navigation_about)
                    true
                }
                R.id.navigation_facilities -> {
                    navController.navigate(R.id.navigation_facilities)
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d("MainActivity", "onStart: Location tracking started")
    }

    override fun onResume() {
        super.onResume()

        // Start to activate GPS sensor (event listener)
        // Fetch data from API
        // Fetch position from GPS
        Log.d("MainActivity", "onResume: Fetch data from API & GPS")
    }

    override fun onPause() {
        super.onPause()

        // Shutdown GPS
        Log.d("MainActivity", "onPause: Pausing tracking")
    }

    override fun onStop() {
        super.onStop()

        // Stop fetch data
        Log.d("MainActivity", "onStop: Stopping location tracking")
    }

    override fun onDestroy() {
        super.onDestroy()

        // Clean up resources
        Log.d("MainActivity", "onDestroy: Cleaning up resources")
    }
}