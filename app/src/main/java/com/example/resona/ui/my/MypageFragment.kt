package com.example.resona.ui.my

import android.os.Bundle
import android.view.View
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
class MypageFragment : Fragment(R.layout.fragment_mypage) {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMypageBinding.bind(view)

        // 마이페이지 진입 시 즉시 본인 프로필 로드
        viewModel.loadMyProfile()

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.profile?.let { profile ->
                    binding.tvNickname.text = profile.nickname

                    Glide.with(this@MypageFragment)
                        .load(profile.profileImage)
                        .placeholder(R.drawable.ic_placeholder)
                        .circleCrop()
                        .into(binding.ivProfileImg)

                    binding.btnMyRecommendations.text = getString(R.string.mypage_menu_my_posts)
                }

                state.error?.let {
                    // 에러 발생 시 처리
                }
            }
        }
    }

    private fun setupListeners() {
        // 내가 쓴 추천글 목록으로 이동
        binding.btnMyRecommendations.setOnClickListener {
            val bundle = Bundle().apply {
                putString("postType", "my")
            }
            findNavController().navigate(R.id.action_to_postList, bundle)
        }

        // 저장한 추천글 목록으로 이동
        binding.btnSavedRecommendations.setOnClickListener {
            val bundle = Bundle().apply {
                putString("postType", "saved")
            }
            findNavController().navigate(R.id.action_to_postList, bundle)
        }

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {
            // 로그아웃 처리 로직 작성
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}