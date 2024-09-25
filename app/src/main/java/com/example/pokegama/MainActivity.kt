package com.example.pokegama

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pokegama.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        supportActionBar?.hide()
        navView.setupWithNavController(navController)
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