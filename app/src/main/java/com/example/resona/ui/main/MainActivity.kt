package com.example.resona.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.resona.R
import com.example.resona.data.event.AuthEventBus
import com.example.resona.data.local.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var authEventBus: AuthEventBus
    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var navHostFragment: NavHostFragment
    private var pendingPostId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getHashKey()

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val topBar = findViewById<View>(R.id.topBar)

        bottomNav.setupWithNavController(navController)

        parseDeepLink(intent)

        lifecycleScope.launch {
            val token = tokenManager.accessToken.firstOrNull()
            val isExpired = tokenManager.isExpired()

            if (token != null && !isExpired) {
                if (navController.currentDestination?.id == R.id.navigation_login) {
                    navController.navigate(
                        R.id.navigation_home,
                        null,
                        NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                    )
                }
                delay(300)
                checkAndNavigateToDeepLink()
            } else {
                navController.navigate(
                    R.id.navigation_login,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                )
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_splash,
                R.id.navigation_login,
                R.id.navigation_login_loading,
                R.id.navigation_onboarding_loading -> {
                    topBar.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                }

                R.id.navigation_onboarding_profile,
                R.id.navigation_onboarding_recommend,
                R.id.navigation_onboarding_result -> {
                    topBar.visibility = View.VISIBLE
                    bottomNav.visibility = View.GONE
                }

                R.id.navigation_home,
                R.id.navigation_mypage,
                R.id.navigation_other_profile -> {
                    topBar.visibility = View.GONE
                    bottomNav.visibility = View.VISIBLE
                }

                else -> {
                    topBar.visibility = View.VISIBLE
                    bottomNav.visibility = View.VISIBLE
                }
            }

            if (destination.id == R.id.navigation_home || destination.id == R.id.navigation_post_list) {
                checkAndNavigateToDeepLink()
            }
        }

        // 1. 토큰 만료 체크 전용
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    if (tokenManager.isExpired()) {
                        tokenManager.clearTokens()
                        authEventBus.emitLogoutOnce()
                    }
                    delay(1000)
                }
            }
        }

        // 2. 이벤트 수집 전용
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authEventBus.event.collect {
                    val currentDest = navController.currentDestination?.id
                    if (currentDest != R.id.navigation_login) {
                        navController.navigate(
                            R.id.navigation_login,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        parseDeepLink(intent)

        lifecycleScope.launch {
            delay(500)
            if (!tokenManager.isExpired()) {
                checkAndNavigateToDeepLink()
            }
        }
    }

    private fun parseDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "https" && uri.host == "resona-guru.store" && uri.path?.startsWith("/post") == true) {
                val postId = uri.lastPathSegment?.toLongOrNull()

                if (postId != null) {
                    pendingPostId = postId
                } else {
                    val queryPostId = uri.getQueryParameter("postId")?.toLongOrNull()
                    if (queryPostId != null) {
                        pendingPostId = queryPostId
                    }
                }
            }
        }
    }

    private fun checkAndNavigateToDeepLink() {
        pendingPostId?.let { postId ->
            try {
                val bundle = Bundle().apply { putLong("postId", postId) }
                navHostFragment.navController.navigate(R.id.navigation_post_detail, bundle)
                pendingPostId = null
            } catch (e: Exception) {
                // 에러 발생 시 처리 (로그 생략)
            }
        }
    }

    private fun getHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Base64.encodeToString(md.digest(), Base64.DEFAULT)
            }
        } catch (e: Exception) {
            // 에러 처리
        }
    }

    fun setTopBarTitle(title: String) {
        findViewById<TextView>(R.id.tv_title).text = title
    }
}