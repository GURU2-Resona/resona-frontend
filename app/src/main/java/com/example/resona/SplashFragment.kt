package com.example.resona

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.resona.data.local.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    @Inject lateinit var tokenManager: TokenManager

    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            delay(2000)

            val token = tokenManager.accessToken.firstOrNull()
            val isExpired = tokenManager.isExpired()

            val navController = findNavController()

            if (!isExpired) {
                navController.navigate(
                    R.id.navigation_home,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_splash, true)
                        .build()
                )
            } else {
                navController.navigate(
                    R.id.navigation_login,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_splash, true)
                        .build()
                )
            }
        }
    }

}