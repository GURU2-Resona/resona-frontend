package com.example.resona

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.resona.data.remote.model.LoginViewModel
import com.example.resona.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login){
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            startKakaoLogin()
        }

        observeLoginResult()
    }

    private fun startKakaoLogin() {
        UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
            if (error != null) {
                // TODO: 에러 UI 처리
                return@loginWithKakaoTalk
            }

            token?.let {
                viewModel.loginWithKakao(it.accessToken)
            }
        }
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            if (result.isNewUser) {
                findNavController()
                    .navigate(R.id.navigation_onboarding_profile)
            } else {
                findNavController()
                    .navigate(R.id.navigation_home)
            }
        }
    }
}