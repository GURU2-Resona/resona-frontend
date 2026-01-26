package com.example.resona.ui.post.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.data.dto.PostDetailResponseDto
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.databinding.FragmentPostDetailShareBinding
import com.example.resona.ui.post.viewmodel.PostViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.template.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class PostDetailShareFragment : Fragment() {

    private var _binding: FragmentPostDetailShareBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()
    private var postId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 하단바 숨기기
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE

        postId = arguments?.getLong("postId") ?: -1L

        if (postId != -1L) {
            loadPostDetail(postId)
        }
    }

    private fun loadPostDetail(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            // 상세조회 API를 통해 닉네임과 데이터를 가져옴
            when (val result = viewModel.repository.getPostDetail(id)) {
                is ApiResult.Success -> {
                    updateUI(result.data)
                }
                is ApiResult.Error -> {
                    Log.e("PostDetailShare", "데이터 로드 실패: ${result.exception.message}")
                }
                else -> Unit
            }
        }
    }

    private fun updateUI(data: PostDetailResponseDto) {
        with(binding) {
            // 1. 텍스트 데이터 연동 (XML ID 기준)
            tvDetailNickname.text = data.writerNickname // 작성자 닉네임
            tvDetailTitle.text = data.title // 게시글 제목
            tvDetailMainText.text = data.content // 게시글 본문
            tvDetailSongTitle.text = data.songTitle // 노래 제목 (썸네일 위 배치)
            tvDetailHash.text = "#${data.categoryName} #${data.sceneName}" // 해시태그

            // 2. 작성자 프로필 이미지 로드
            Glide.with(this@PostDetailShareFragment)
                .load(data.writerProfileImage)
                .placeholder(R.drawable.ic_placeholder)
                .circleCrop()
                .into(ivProfile)

            // 3. 유튜브 썸네일 이미지 로드
            val videoId = extractVideoId(data.songUrl)
            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"

            Glide.with(this@PostDetailShareFragment)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_thumnail_placeholder)
                .centerCrop()
                .into(ivDetailAlbumArt) // iv_detail_album_art ID 사용

            // 4. 공유 버튼 리스너 (기존 btn_detail_share ID 유지)
            btnDetailShare.setOnClickListener {
                sendKakaoShare(data, thumbnailUrl)
            }

            // 5. 노래 전체 들으러 가기 버튼 (유튜브 링크 연결)
            btnListenAll.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.songUrl))
                startActivity(intent)
            }
        }
    }

    private fun sendKakaoShare(data: PostDetailResponseDto, imageUrl: String) {
        val defaultFeed = FeedTemplate(
            content = Content(
                title = data.title,
                description = data.content,
                imageUrl = imageUrl,
                link = Link(webUrl = data.songUrl, mobileWebUrl = data.songUrl)
            ),
            buttons = listOf(
                Button("앱에서 보기", Link(androidExecutionParams = mapOf("postId" to data.postId.toString())))
            )
        )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(requireContext())) {
            ShareClient.instance.shareDefault(requireContext(), defaultFeed) { result, error ->
                if (error == null && result != null) startActivity(result.intent)
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