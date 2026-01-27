package com.example.resona.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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

    // 1. PostViewModel 주입
    private val viewModel: PostViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    private val youtubeVideoId = "hrXCP0xeoA8"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupMusicCard()
        setupPreviewList() // 어댑터 초기화
        observeViewModel() // 데이터 관찰 시작

        // 2. 추천글 목록 전체 조회 API 호출
        viewModel.loadPosts()

        binding.btnGoPostList.setOnClickListener {
            findNavController().navigate(R.id.navigation_post_list)
        }
    }

    private fun setupMusicCard() {
        val thumbnailUrl = "https://img.youtube.com/vi/$youtubeVideoId/0.jpg"

        Glide.with(this)
            .load(thumbnailUrl)
            .placeholder(R.color.neutral_400)
            .into(binding.ivMusicThumbnail)

        binding.cardMusic.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=$youtubeVideoId")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        binding.tvMusicName.text = "행운을 빌어줘"
        binding.tvMusicArtist.text = "원필"
    }

    /**
     * 3. 리사이클러뷰 및 어댑터 초기 설정
     */
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
    /**
     * 4. ViewModel 상태 관찰 및 데이터 필터링
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // 데이터가 성공적으로 로드되었을 때
                if (state.posts.isNotEmpty()) {
                    // 전체 리스트 중 상위 3개만 추출하여 어댑터에 전달
                    postAdapter.updateData(state.posts.take(3))
                }

                // 로딩 중일 때 처리 (필요 시)
                if (state.isLoading) {
                    // binding.loadingBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}