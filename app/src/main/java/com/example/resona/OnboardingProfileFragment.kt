package com.example.resona

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resona.data.remote.model.NicknameViewModel
import com.example.resona.data.remote.model.ProfileImageViewModel
import com.example.resona.databinding.FragmentOnboardingProfileBinding
import com.example.resona.ui.main.MainActivity
import kotlin.getValue

class OnboardingProfileFragment : Fragment(R.layout.fragment_onboarding_profile) {
    private var _binding: FragmentOnboardingProfileBinding? = null
    private val binding get() = _binding!!
    private val imageViewModel: ProfileImageViewModel by activityViewModels()
    private val nicknameViewModel : NicknameViewModel by activityViewModels()

    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingProfileBinding.bind(view)
        (activity as? MainActivity)?.setTopBarTitle("정보 입력")

        imageViewModel.loadProfileImage()
        imageViewModel.profileImageResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                Glide.with(this)
                    .load(response.profileImage)
                    .circleCrop()
                    .into(binding.ivImage)
            }
        }

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
            nicknameViewModel.saveNickname(binding.etNickname.text.toString())
            findNavController().navigate(R.id.navigation_onboarding_recommend)
        }

    }
}