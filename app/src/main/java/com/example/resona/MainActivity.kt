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

        // 1. 네비게이션을 담는 그릇(NavHost) 찾기
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // 2. 실제 제어 도구(NavController) 가져오기
        val navController = navHostFragment.navController

        // 3. 바텀 네비게이션 뷰 찾기
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val topBar = findViewById<View>(R.id.topBar)

        // 4. 바텀 네비게이션과 네비게이션 컨트롤러 연결하기
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_splash, R.id.navigation_login -> {
                    topBar.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                }
                R.id.navigation_onboarding_profile -> {
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