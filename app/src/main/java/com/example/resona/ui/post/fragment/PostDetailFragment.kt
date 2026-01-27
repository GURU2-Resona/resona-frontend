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
import com.example.resona.databinding.FragmentPostDetailBinding
import com.example.resona.ui.post.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()
    private var postId: Long = -1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getLong("postId") ?: -1L
        if (postId != -1L) {
            viewModel.loadPostDetail(postId)
            observeUiState()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.postDetail?.let { data ->
                    if (data.isMine) {
                        navigateToShare(data)
                    } else {
                        bindDataToUI(data)
                    }
                }
                state.error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindDataToUI(data: PostDetailResponseDto) {
        with(binding) {
            tvDetailTitle.text = data.title
            tvDetailSongTitle.text = data.songTitle
            tvDetailMainText.text = data.content
            tvDetailNickname.text = data.writerNickname
            tvDetailHash.text = "#${data.categoryName} #${data.sceneName}"

            // 프로필 이미지 로드
            Glide.with(this@PostDetailFragment)
                .load(data.writerProfileImage)
                .placeholder(R.drawable.ic_placeholder)
                .circleCrop()
                .into(ivProfile)

            // 유튜브 썸네일 로드
            val videoId = extractVideoId(data.songUrl)
            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"

            Glide.with(this@PostDetailFragment)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_thumnail_placeholder)
                .centerCrop()
                .into(ivDetailAlbumArt)

            // 스크랩 아이콘 설정 및 리스너
            ivSave.setImageResource(if (data.isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark)
            ivSave.setOnClickListener {
                viewModel.toggleScrap(data.postId)
            }

            // '노래 전체 들으러 가기' 버튼
            btnListenAll.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.songUrl))
                startActivity(intent)
            }

            // 프로필 클릭 시 이동
            ivProfile.setOnClickListener {
                val bundle = Bundle().apply { putLong("memberId", data.writerId) }
                findNavController().navigate(R.id.action_postDetail_to_otherProfile, bundle)
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

    private fun extractVideoId(url: String?): String? {
        val pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=)[^#&?\\n]*"
        val matcher = Pattern.compile(pattern).matcher(url ?: "")
        return if (matcher.find()) matcher.group() else null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}