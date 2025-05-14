package com.mediaplus.app.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mediaplus.app.R
import com.mediaplus.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)// Set up search FAB
        binding.fabSearch.setOnClickListener {
            // Launch search activity
            val intent = android.content.Intent(this, com.mediaplus.app.ui.search.SearchActivityJava::class.java)
            startActivity(intent)
        }
    }
}
