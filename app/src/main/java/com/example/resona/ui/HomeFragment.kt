package com.example.resona.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resona.R
import com.example.resona.databinding.FragmentHomeBinding
import com.example.resona.ui.main.MainActivity
import com.example.resona.ui.post.adapter.PostAdapter
import com.example.resona.ui.post.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        (activity as? MainActivity)?.findViewById<View>(R.id.topBar)?.visibility = View.GONE

        setupRecommendButton()
        setupPreviewList()
        observeViewModel()

        viewModel.loadPosts()

        binding.btnGoPostList.setOnClickListener {
            findNavController().navigate(R.id.navigation_post_list)
        }
    }

    private fun setupRecommendButton() {
        binding.btnGoRecommend.setOnClickListener {
            // 온보딩의 추천 카테고리 선택 화면으로 이동
            // (nav_graph.xml에 정의된 id 사용)
            findNavController().navigate(R.id.navigation_onboarding_recommend)
        }
    }


    private fun setupPreviewList() {
        postAdapter = PostAdapter(emptyList()).apply {
            onItemClick = { post ->
                val bundle = Bundle().apply {
                    putLong("postId", post.postId.toLong())
                }
                findNavController().navigate(R.id.navigation_post_detail, bundle)
            }
        }
        binding.rvHomePreview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = postAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.posts.isNotEmpty()) {
                    postAdapter.updateData(state.posts.take(3))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}