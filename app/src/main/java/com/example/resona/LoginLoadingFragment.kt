package com.example.resona

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.resona.data.remote.model.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginLoadingFragment: Fragment(R.layout.fragment_login_loading) {
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLoginResult()
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                if (result.isNewUser) {
                    findNavController().navigate(
                        R.id.navigation_onboarding_profile
                    )
                } else {
                    findNavController().navigate(
                        R.id.navigation_home
                    )
                }
            }
        }
    }
}