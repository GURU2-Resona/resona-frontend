package com.example.resona.ui.post.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.data.dto.PostDetailResponseDto
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.databinding.FragmentPostDetailBinding
import com.example.resona.ui.post.viewmodel.PostViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()
    private var postId: Long = -1L
    private var isBookmarked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE

        postId = arguments?.getLong("postId") ?: -1L
        if (postId != -1L) loadPostDetail()
    }

    private fun loadPostDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = viewModel.repository.getPostDetail(postId)
            if (result is ApiResult.Success) {
                val data = result.data
                if (data.isMine) { // 내 글인 경우
                    navigateToShare(data)
                } else { // 남의 글인 경우
                    bindDataToUI(data)
                }
            }
        }
    }

    private fun navigateToShare(data: PostDetailResponseDto) {
        val bundle = Bundle().apply {
            putLong("postId", data.postId)
            putString("finalSubject", data.title)
            putString("finalContent", data.content)
            putString("finalTag", "#${data.categoryName} #${data.sceneName}") // 해시태그 합치기
            putString("videoId", extractVideoId(data.songUrl))
        }
        findNavController().navigate(R.id.action_postDetail_to_postDetailShare, bundle)
    }

    private fun bindDataToUI(data: PostDetailResponseDto) {
        with(binding) {
            tvDetailSongTitle.text = data.title
            tvDetailMainText.text = data.content
            tvDetailNickname.text = data.writerNickname
            tvDetailHash.text = "#${data.categoryName} #${data.sceneName}" // 카테고리 + 상황

            Glide.with(this@PostDetailFragment)
                .load(data.writerProfileImage)
                .placeholder(R.drawable.ic_placeholder)
                .circleCrop()
                .into(ivProfile)

            isBookmarked = data.isSaved
            ivDetailBookmark.setImageResource(if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark)

            val videoId = extractVideoId(data.songUrl)
            if (videoId != null) {
                viewLifecycleOwner.lifecycle.addObserver(detailYoutubePlayer)
                detailYoutubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                })
            }
        }
    }

    private fun extractVideoId(url: String?): String? {
        val pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=)[^#&?\\n]*"
        val matcher = Pattern.compile(pattern).matcher(url ?: "")
        return if (matcher.find()) matcher.group() else null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.VISIBLE
        _binding = null
    }
}