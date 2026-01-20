package com.example.resona

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment(R.layout.fragment_login){
    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val btnLogin = view.findViewById<ImageButton>(R.id.btn_login)
        btnLogin.setOnClickListener {
            // 카카오 로그인 구현
            // 첫 가입일 경우
            findNavController().navigate(R.id.navigation_home)
            // 기존 유저일 경우
            //findNavController().navigate(R.id.navigation_home)
        }
    }
}