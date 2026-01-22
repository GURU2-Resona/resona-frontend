package com.example.resona.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resona.R
import com.example.resona.databinding.FragmentMypageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MypageFragment : Fragment(R.layout.fragment_mypage) {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMypageBinding.bind(view)

        // 나의 추천글 (postType = "my")
        binding.btnMyRecommendations.setOnClickListener {
            val bundle = Bundle().apply { putString("postType", "my") }
            findNavController().navigate(R.id.action_to_postList, bundle)
        }

        // 저장한 추천글 (postType = "saved")
        binding.btnSavedRecommendations.setOnClickListener {
            val bundle = Bundle().apply { putString("postType", "saved") }
            findNavController().navigate(R.id.action_to_postList, bundle)
        }

        binding.btnLogout.setOnClickListener { /* 로그아웃 로직 */ }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}