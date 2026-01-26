package com.example.resona.ui.post.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.resona.R
import com.example.resona.data.remote.model.ApiResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.example.resona.ui.post.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private var isBookmarked = false
    private val viewModel: PostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.visibility = View.GONE

        val tvTitle = view.findViewById<TextView>(R.id.tv_detail_song_title)
        val tvMainText = view.findViewById<TextView>(R.id.tv_detail_main_text)
        val tvHash = view.findViewById<TextView>(R.id.tv_detail_hash)
        val ivBookmark = view.findViewById<ImageView>(R.id.iv_detail_bookmark)
        val ivAlbumArt = view.findViewById<ImageView>(R.id.iv_detail_album_art)
        val youtubePlayerView = view.findViewById<YouTubePlayerView>(R.id.detail_youtube_player)

        val postId = arguments?.getLong("postId") ?: -1L

        viewLifecycleOwner.lifecycle.addObserver(youtubePlayerView)

        if (postId != -1L) {
            viewLifecycleOwner.lifecycleScope.launch {
                val result = viewModel.repository.getPostDetail(1L, postId)
                when (result) {
                    is ApiResult.Success -> {
                        val data = result.data
                        tvTitle?.text = data.title
                        tvMainText?.text = data.content
                        tvHash?.text = "#${data.categoryName} #${data.sceneName}"
                        isBookmarked = data.isSaved
                        ivBookmark?.setImageResource(
                            if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
                        )

                        val vId = data.songUrl.split("v=").lastOrNull()
                        if (!vId.isNullOrEmpty()) {
                            setupYoutubePlayer(youtubePlayerView, ivAlbumArt, vId)
                        }
                    }
                    is ApiResult.Error -> {
                        Log.e("API_ERROR", result.exception.message.toString())
                        Toast.makeText(context, "데이터 로드 실패", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        } else {
            val title = arguments?.getString("finalSubject")
            val content = arguments?.getString("finalContent")
            val tags = arguments?.getString("finalTag")
            val videoId = arguments?.getString("videoId")

            tvTitle?.text = title ?: "입력된 제목이 없습니다"
            tvMainText?.text = content ?: "입력된 내용이 없습니다"
            tvHash?.text = tags ?: "#카테고리미정"

            if (!videoId.isNullOrEmpty()) {
                setupYoutubePlayer(youtubePlayerView, ivAlbumArt, videoId)
            }
        }

        ivBookmark?.setOnClickListener {
            if (postId != -1L) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = viewModel.repository.toggleScrap(1L, postId)
                    when (result) {
                        is ApiResult.Success -> {
                            val message = result.data
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            isBookmarked = (message == "스크랩 성공")
                            ivBookmark.setImageResource(
                                if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
                            )
                        }
                        is ApiResult.Error -> {
                            Log.e("API_ERROR", "스크랩 요청 실패: ${result.exception.message}")
                            Toast.makeText(requireContext(), "스크랩 실패", Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupYoutubePlayer(
        playerView: YouTubePlayerView,
        albumArt: ImageView?,
        videoId: String
    ) {
        albumArt?.visibility = View.GONE
        playerView.visibility = View.VISIBLE
        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.visibility = View.VISIBLE
    }
}