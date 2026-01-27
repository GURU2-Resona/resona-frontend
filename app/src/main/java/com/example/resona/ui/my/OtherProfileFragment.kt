package com.example.resona.ui.my

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.databinding.FragmentMypageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtherProfileFragment : Fragment(R.layout.fragment_mypage) {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMypageBinding.bind(view)

        val memberId = arguments?.getLong("memberId", -1L) ?: -1L

        binding.btnSavedRecommendations.isVisible = false
        binding.btnLogout.isVisible = false

        if (memberId != -1L) {
            viewModel.loadMemberProfile(memberId)
            observeViewModel(memberId)
        }
    }

    private fun observeViewModel(memberId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.profile?.let { profile ->
                    binding.tvNickname.text = profile.nickname
                    binding.btnMyRecommendations.text = "${profile.nickname}님이 쓴 글"

                    Glide.with(this@OtherProfileFragment)
                        .load(profile.profileImage)
                        .placeholder(R.drawable.ic_placeholder)
                        .centerCrop()
                        .circleCrop()
                        .into(binding.ivProfileImg)

                    binding.btnMyRecommendations.setOnClickListener {
                        val bundle = Bundle().apply {
                            putString("postType", "other")
                            putLong("targetMemberId", memberId)
                        }
                        findNavController().navigate(R.id.action_otherProfile_to_postList, bundle)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}