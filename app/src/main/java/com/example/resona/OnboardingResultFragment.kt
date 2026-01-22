package com.example.resona

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

class OnboardingResultFragment : Fragment(R.layout.fragment_onboarding_result) {

    private val youtubeVideoId = "hrXCP0xeoA8" // 실제 재생 가능한 ID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivThumbnail = view.findViewById<ImageView>(R.id.iv_thumbnail)
        val tvSongTitle = view.findViewById<TextView>(R.id.tv_song_title)
        val tvArtist = view.findViewById<TextView>(R.id.tv_artist)

        // Glide로 썸네일 로딩
        val thumbnailUrl = "https://img.youtube.com/vi/$youtubeVideoId/0.jpg"
        Glide.with(this)
            .load(thumbnailUrl)
            .placeholder(R.color.neutral_400)
            .into(ivThumbnail)

        // 클릭 시 유튜브 앱 또는 브라우저로 이동
        ivThumbnail.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=$youtubeVideoId")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // 텍스트 예시 세팅
        tvSongTitle.text = "행운을 빌어줘"
        tvArtist.text = "원필"

        val homeButton = view.findViewById<Button>(R.id.btn_home);
        homeButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }

        val listenButton = view.findViewById<Button>(R.id.btn_listen_full);
        listenButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=$youtubeVideoId")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val postButton = view.findViewById<Button>(R.id.btn_write_recommend);
        postButton.setOnClickListener {
            // findNavController().navigate()
        }
    }
}