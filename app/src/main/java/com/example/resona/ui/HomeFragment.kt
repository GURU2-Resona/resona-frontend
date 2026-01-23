package com.example.resona.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.databinding.FragmentHomeBinding
import com.example.resona.ui.main.MainActivity // MainActivity import 필요
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // 온보딩 예시 유튜브 ID
    private val youtubeVideoId = "hrXCP0xeoA8"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        (activity as? MainActivity)?.findViewById<View>(R.id.topBar)?.visibility = View.GONE

        // 1. 오늘의 음악 추천
        setupMusicCard()

        // 2. 추천글 보기 (화살표) 클릭 이벤트
        binding.btnGoPostList.setOnClickListener {
            // 네비게이션 그래프 ID로 이동
            findNavController().navigate(R.id.navigation_post_list)
        }

        // 3. 추천글 목록 미리보기 (3개만 표시)
        setupPreviewList()
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

    private fun setupPreviewList() {
        val dummyData = generateDummyData()
        val previewData = dummyData.take(3) // 상위 3개만

        val adapter = PostAdapter(previewData)

        binding.rvHomePreview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            // 스크롤 중첩 방지
            isNestedScrollingEnabled = true
        }
    }

    private fun generateDummyData(): List<PostModel> {
        val list = mutableListOf<PostModel>()
        val cats = listOf("음악", "영상", "책", "ASMR")
        val sits = listOf("운동", "공부", "휴식", "새벽")

        for (i in 1..10) {
            val c = cats.random()
            val s = sits.random()
            val prefix = "[추천]"

            list.add(PostModel(
                title = "$prefix $c $i",
                subhead = "$s 할 때 좋은 콘텐츠",
                category = c,
                situation = s
            ))
        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}