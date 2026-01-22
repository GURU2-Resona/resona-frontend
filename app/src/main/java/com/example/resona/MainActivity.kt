package com.example.resona

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val topBar = findViewById<View>(R.id.topBar)

        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_post_list -> {
                    bottomNav.visibility = View.GONE
                }
                else -> {
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_splash,
                R.id.navigation_login,
                R.id.navigation_post_list -> {
                    topBar.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                }
                R.id.navigation_onboarding_profile,
                R.id.navigation_onboarding_recommend,
                R.id.navigation_onboarding_result-> {
                    topBar.visibility = View.VISIBLE
                    bottomNav.visibility = View.GONE
                }
                else ->  {
                    topBar.visibility = View.VISIBLE
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    fun setTopBarTitle(title: String) {
        findViewById<TextView>(R.id.tv_title).text = title
    }
}