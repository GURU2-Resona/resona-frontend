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
import com.example.resona.data.remote.model.PostDetailResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
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
                    // 1. 서버에서 응답을 받아옴 (지애님은 Response<BaseResponse<T>> 형태를 씀)
                    val response = service.getPostDetail(1L, postId)

                    if (response.isSuccessful) {
                        // 2. response.body()를 통해 BaseResponse에 접근
                        response.body()?.let { baseResponse ->
                            if (baseResponse.isSuccess) {
                                // 3. baseResponse.result가 우리가 원하는 데이터!
                                baseResponse.result?.let { data ->
                                    tvTitle?.text = data.title
                                    tvMainText?.text = data.content
                                    tvHash?.text = "#${data.categoryName} #${data.sceneName}"
                                    isBookmarked = data.isSaved
                                    ivBookmark?.setImageResource(if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark)

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

        viewLifecycleOwner.lifecycle.addObserver(youtubePlayerView)

        ivBookmark?.setOnClickListener {
            if (postId != -1L) {
                val service = RetrofitClient.getService().create(PostService::class.java)
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = service.toggleScrap(1L, postId)
                        if (response.isSuccessful) {
                            response.body()?.let { baseResponse ->
                                val message = baseResponse.result.toString()
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                isBookmarked = (message == "스크랩 성공")
                                ivBookmark.setImageResource(if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "스크랩 요청 실패: ${e.message}")
                    }
                }
            }
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