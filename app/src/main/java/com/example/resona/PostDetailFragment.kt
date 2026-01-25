package com.example.resona.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.resona.R
import com.example.resona.RetrofitClient
import com.example.resona.data.remote.api.PostService
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PostDetailFragment : Fragment() {

    private var isBookmarked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }

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

        if (postId != -1L) {
            val service = RetrofitClient.getService().create(PostService::class.java)
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = service.getPostDetail(1L, postId)
                    if (response.isSuccessful) {
                        response.body()?.let { baseResponse ->
                            if (baseResponse.isSuccess) {
                                baseResponse.result?.let { data ->
                                    tvTitle?.text = data.title
                                    tvMainText?.text = data.content
                                    tvHash?.text = "#${data.categoryName} #${data.sceneName}"

                                    // 저장 상태 반영
                                    isBookmarked = data.isSaved
                                    updateBookmarkIcon(ivBookmark)

                                    val vId = data.songUrl.split("v=").lastOrNull()
                                    if (!vId.isNullOrEmpty()) {
                                        setupYoutubePlayer(youtubePlayerView, ivAlbumArt, vId)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", e.message.toString())
                }
            }
        }

        // 북마크 클릭 이벤트
        ivBookmark?.setOnClickListener {
            if (postId != -1L) {
                val service = RetrofitClient.getService().create(PostService::class.java)
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = service.toggleScrap(1L, postId)
                        if (response.isSuccessful && response.body()?.isSuccess == true) {
                            // 현재 상태를 반전시키고 아이콘 업데이트
                            isBookmarked = !isBookmarked
                            updateBookmarkIcon(ivBookmark)

                            val msg = if (isBookmarked) "내 보관함에 저장되었습니다." else "저장이 취소되었습니다."
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "스크랩 요청 실패: ${e.message}")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycle.addObserver(youtubePlayerView)
    }

    private fun updateBookmarkIcon(ivBookmark: ImageView?) {
        if (isBookmarked) {
            ivBookmark?.setImageResource(R.drawable.ic_bookmark_filled)
        } else {
            ivBookmark?.setImageResource(R.drawable.ic_bookmark)
        }
    }

    private fun setupYoutubePlayer(playerView: YouTubePlayerView, albumArt: ImageView?, videoId: String) {
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