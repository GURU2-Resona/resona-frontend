package com.example.resona

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        view.postDelayed({
            findNavController().navigate(R.id.navigation_login)
        }, 2000)
    }

}