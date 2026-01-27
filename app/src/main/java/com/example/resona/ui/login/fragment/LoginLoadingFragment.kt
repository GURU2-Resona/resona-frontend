package com.example.resona.ui.login.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.resona.R
import com.example.resona.data.event.AuthEventBus
import com.example.resona.ui.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginLoadingFragment: Fragment(R.layout.fragment_login_loading) {
    private val viewModel: LoginViewModel by activityViewModels()
    @Inject
    lateinit var authEventBus: AuthEventBus

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLoginResult()
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                authEventBus.reset()
                Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                if (result.isNewUser) {
                    findNavController().navigate(
                        R.id.navigation_onboarding_profile,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_login, true)
                            .build()
                    )
                } else {
                    findNavController().navigate(
                        R.id.navigation_home,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_login, true)
                            .build()
                    )
                }
            }
        }
    }
}