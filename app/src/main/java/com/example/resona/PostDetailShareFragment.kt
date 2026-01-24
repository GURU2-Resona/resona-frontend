package com.example.resona

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class PostDetailShareFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_detail_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.visibility = View.GONE

        val tvTitle = view.findViewById<TextView>(R.id.tv_detail_song_title)
        val tvMainText = view.findViewById<TextView>(R.id.tv_detail_main_text)
        val tvHash = view.findViewById<TextView>(R.id.tv_detail_hash)
        val ivShare = view.findViewById<ImageView>(R.id.iv_detail_share)
        val ivAlbumArt = view.findViewById<ImageView>(R.id.iv_detail_album_art)
        val youtubePlayerView = view.findViewById<YouTubePlayerView>(R.id.detail_youtube_player)

        val title = arguments?.getString("finalSubject")
        val content = arguments?.getString("finalContent")
        val tags = arguments?.getString("finalTag")
        val videoId = arguments?.getString("videoId")

        tvTitle?.text = title ?: "제목 없음"
        tvMainText?.text = content ?: "내용 없음"
        tvHash?.text = tags ?: "#태그없음"

        viewLifecycleOwner.lifecycle.addObserver(youtubePlayerView)

        if (!videoId.isNullOrEmpty()) {
            ivAlbumArt?.visibility = View.GONE
            youtubePlayerView?.visibility = View.VISIBLE
            youtubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            })
        }

        ivShare?.setOnClickListener {
            Toast.makeText(requireContext(), "공유하기 기능을 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.visibility = View.VISIBLE
    }
}