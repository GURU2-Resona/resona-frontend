package com.example.resona.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.resona.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 해시키를 확인하기 위해 함수 호출
        getHashKey()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val topBar = findViewById<View>(R.id.topBar)

        bottomNav.setupWithNavController(navController)

// MainActivity.kt 수정 부분
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            val destinationName = resources.getResourceEntryName(destination.id)
            Log.d("NavDebug", "이동한 화면: $destinationName (ID: ${destination.id})")

            when (destination.id) {
                // 1. 완전히 숨겨야 하는 화면들
                R.id.navigation_splash,
                R.id.navigation_login,
                R.id.navigation_login_loading,
                R.id.navigation_post_detail,        // 상세 페이지
                R.id.navigation_post_detail_share,  // 공유 페이지
                R.id.navigation_other_profile -> {  // 타인 프로필
                    Log.d("NavDebug", "결과: 탑바/바텀바 모두 숨김")
                    topBar.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                }

                // 2. 바텀바만 숨겨야 하는 화면들 (온보딩 등)
                R.id.navigation_onboarding_profile,
                R.id.navigation_onboarding_recommend,
                R.id.navigation_onboarding_result -> {
                    Log.d("NavDebug", "결과: 탑바 표시 / 바텀바 숨김")
                    topBar.visibility = View.VISIBLE
                    bottomNav.visibility = View.GONE
                }

                // 3. 바텀바가 반드시 보여야 하는 메인 화면들
                R.id.navigation_home,
                R.id.navigation_post_list,
                R.id.navigation_mypage -> {
                    Log.d("NavDebug", "결과: 탑바 표시 / 바텀바 표시")
                    topBar.visibility = View.VISIBLE
                    bottomNav.visibility = View.VISIBLE
                }

                // 4. 그 외 정의되지 않은 모든 화면
                else -> {
                    Log.e("NavDebug", "알 수 없는 화면($destinationName) 진입 - 기본값(표시) 적용")
                    topBar.visibility = View.VISIBLE
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    // 해시키 추출 함수
    private fun getHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("KeyHash", "해시키: $keyHash")
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "해시키를 찾을 수 없습니다.", e)
        }
    }

    fun setTopBarTitle(title: String) {
        findViewById<TextView>(R.id.tv_title).text = title
    }
}