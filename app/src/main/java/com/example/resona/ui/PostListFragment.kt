package com.example.resona.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.resona.R
import com.example.resona.databinding.FragmentPostListBinding

class PostListFragment : Fragment(R.layout.fragment_post_list) {
    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        // 전달받은 타입 확인
        val type = arguments?.getString("postType") ?: "MY"

        // 타입에 따라 텍스트 변경 (TODO: 타입에 따라 다른 API 연동으로 변경 예정)
        binding.tvTempTitle.text = when (type) {
            "MY" -> "나의 추천글 화면입니다"
            "SAVED" -> "저장한 추천글 화면입니다"
            else -> "준비 중인 화면입니다 ($type)"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}