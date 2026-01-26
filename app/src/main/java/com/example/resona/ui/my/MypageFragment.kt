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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MypageFragment : Fragment(R.layout.fragment_mypage) {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMypageBinding.bind(view)

        val isMyProfile = arguments?.getBoolean("isMyProfile", true) ?: true
        val memberId = arguments?.getLong("memberId", -1L) ?: -1L

        if (isMyProfile) {
            // 본인 프로필 - 현재는 하드코딩이지만 나중에 API 추가 가능
            setupMyProfileUI()
        } else {
            // 타인 프로필 - API로 데이터 조회
            if (memberId != -1L) {
                viewModel.loadMemberProfile(memberId)
                observeViewModel()
            }
        }

        setupListeners(isMyProfile, memberId)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.profile?.let { profile ->
                    // 타인 프로필 데이터 바인딩
                    binding.tvNickname.text = profile.nickname
                    binding.btnMyRecommendations.text = "${profile.nickname}님이 쓴 글"

                    Glide.with(this@MypageFragment)
                        .load(profile.profileImage)
                        .placeholder(R.drawable.ic_placeholder)
                        .circleCrop()
                        .into(binding.ivProfileImg)
                }

                // 타인 프로필일 때 버튼 가시성 제어
                binding.btnSavedRecommendations.isVisible = false
                binding.btnLogout.isVisible = false
            }
        }
    }

    private fun setupMyProfileUI() {
        // 본인 프로필 UI 설정
        binding.tvNickname.text = "나의 닉네임"
        binding.btnMyRecommendations.text = getString(R.string.mypage_menu_my_posts)
        binding.btnSavedRecommendations.isVisible = true
        binding.btnLogout.isVisible = true

        // TODO: 본인 프로필 API 추가되면 아래처럼 변경
        // viewModel.loadMyProfile()
        // observeMyProfileViewModel()
    }

    private fun setupListeners(isMyProfile: Boolean, memberId: Long) {
        // 추천글 목록 이동
        binding.btnMyRecommendations.setOnClickListener {
            val bundle = Bundle().apply {
                if (isMyProfile) {
                    putString("postType", "my")
                } else {
                    putString("postType", "other")
                    putLong("targetMemberId", memberId)
                }
            }
            findNavController().navigate(R.id.action_to_postList, bundle)
        }

        // 저장한 추천글 (본인일 때만 작동)
        binding.btnSavedRecommendations.setOnClickListener {
            val bundle = Bundle().apply { putString("postType", "saved") }
            findNavController().navigate(R.id.action_to_postList, bundle)
        }

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {
            // TODO: 로그아웃 처리 로직
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}