package com.example.resona

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.resona.data.remote.model.LoginViewModel
import com.example.resona.databinding.FragmentLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            startKakaoLogin()
        }
    }

    private fun startKakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireActivity()) { token, error ->
                handleLoginResult(token, error)
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireActivity()) { token, error ->
                handleLoginResult(token, error)
            }
        }
    }

    private fun handleLoginResult(token: OAuthToken?, error: Throwable?) {
        if (error != null) {
            Log.e("KakaoLogin", "카카오 로그인 실패", error)
        } else if (token != null) {
            findNavController().navigate(R.id.navigation_login_loading)
            Log.d("KakaoToken", "카카오 액세스 토큰: ${token.accessToken}")
            viewModel.loginWithKakao(token.accessToken)
        }
    }
}