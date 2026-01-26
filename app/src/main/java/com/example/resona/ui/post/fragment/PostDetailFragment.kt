package com.example.resona.ui.post.fragment

import android.content.Intent
import android.net.Uri
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()
    private var postId: Long = -1L
    private var isSaved = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 하단바 숨기기
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE

        postId = arguments?.getLong("postId") ?: -1L
        if (postId != -1L) loadPostDetail()
    }

    private fun loadPostDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = viewModel.repository.getPostDetail(postId)
            if (result is ApiResult.Success) {
                val data = result.data
                if (data.isMine) { // 내 글인 경우 공유 화면으로 이동
                    navigateToShare(data)
                } else { // 남의 글인 경우 현재 화면 데이터 바인딩
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
            putString("finalTag", "#${data.categoryName} #${data.sceneName}")
            putString("videoId", extractVideoId(data.songUrl))
        }
        findNavController().navigate(R.id.action_postDetail_to_postDetailShare, bundle)
    }

    private fun bindDataToUI(data: PostDetailResponseDto) {
        with(binding) {
            // 1. 텍스트 연동 (XML ID: tv_detail_song_title, tv_detail_main_text 등)
            tvDetailSongTitle.text = data.title
            tvDetailMainText.text = data.content
            tvDetailNickname.text = data.writerNickname // 닉네임 연동
            tvDetailHash.text = "#${data.categoryName} #${data.sceneName}"

            // 2. 프로필 이미지 로드
            Glide.with(this@PostDetailFragment)
                .load(data.writerProfileImage)
                .placeholder(R.drawable.ic_placeholder)
                .circleCrop()
                .into(ivProfile)

            // 3. 유튜브 썸네일 이미지 로드 (요청대로 이미지만 표시)
            val videoId = extractVideoId(data.songUrl)
            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"

            Glide.with(this@PostDetailFragment)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_thumnail_placeholder)
                .centerCrop()
                .into(ivDetailAlbumArt)

            // 4. 북마크 상태 초기화 및 클릭 리스너 (ID: iv_save)
            isSaved = data.isSaved
            ivSave.setImageResource(if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark)
            ivSave.setOnClickListener { toggleBookmark() }

            // 5. 노래 전체 들으러 가기 버튼 연동
            btnListenAll.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.songUrl))
                startActivity(intent)
            }
        }
    }

    private fun toggleBookmark() {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = viewModel.repository.toggleScrap(postId)
            if (result is ApiResult.Success) {
                isSaved = !isSaved
                binding.ivSave.setImageResource(
                    if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
                )
                val msg = if (isSaved) "스크랩되었습니다." else "스크랩이 취소되었습니다."
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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
        // 화면을 나갈 때 하단바 다시 표시
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.VISIBLE
        _binding = null
    }
}