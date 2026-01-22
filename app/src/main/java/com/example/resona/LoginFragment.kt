package com.example.resona

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resona.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login){
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            // 카카오 로그인 구현
            // 첫 가입일 경우
            findNavController().navigate(R.id.navigation_onboarding_profile)
            // 기존 유저일 경우
            //findNavController().navigate(R.id.navigation_home)
        }
    }
}