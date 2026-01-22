package com.example.resona

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resona.databinding.FragmentOnboardingProfileBinding

class OnboardingProfileFragment : Fragment(R.layout.fragment_onboarding_profile) {
    private var _binding: FragmentOnboardingProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingProfileBinding.bind(view)
        (activity as? MainActivity)?.setTopBarTitle("정보 입력")

        binding.btnNext.isEnabled = false

        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                val nickname = s.toString().trim()
                binding.btnNext.isEnabled = nickname.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.navigation_onboarding_recommend)
        }

    }
}