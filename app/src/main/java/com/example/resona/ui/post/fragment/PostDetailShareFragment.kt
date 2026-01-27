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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.data.dto.PostDetailResponseDto
import com.example.resona.databinding.FragmentPostDetailShareBinding
import com.example.resona.ui.main.MainActivity
import com.example.resona.ui.post.viewmodel.PostViewModel
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailShareBinding.inflate(inflater, container, false)
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.postDetail?.let { data -> updateUI(data) }
                    state.error?.let { Log.e("PostDetailShare", "에러 발생: $it") }
                }
            }
        }
    }

    private fun updateUI(data: PostDetailResponseDto) {
        with(binding) {
            tvDetailNickname.text = data.writerNickname
            tvDetailTitle.text = data.title
            tvDetailMainText.text = data.content
            tvDetailSongTitle.text = data.songTitle
            tvDetailHash.text = "#${data.categoryName} #${data.sceneName}"

            Glide.with(this@PostDetailShareFragment)
                .load(data.writerProfileImage)
                .placeholder(R.drawable.ic_placeholder)
                .circleCrop()
                .into(ivProfile)

            val videoId = extractVideoId(data.songUrl)
            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"

            Glide.with(this@PostDetailShareFragment)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_thumnail_placeholder)
                .centerCrop()
                .into(ivDetailAlbumArt)

            btnDetailShare.setOnClickListener { sendKakaoShare(data, thumbnailUrl) }
            btnListenAll.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.songUrl)))
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
            buttons = listOf(Button("앱에서 보기", Link(androidExecutionParams = mapOf("postId" to data.postId.toString()))))
        )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(requireContext())) {
            ShareClient.instance.shareDefault(requireContext(), defaultFeed) { result, error ->
                if (error == null && result != null) startActivity(result.intent)
            }
        } else {
            Log.d("KakaoShare", "카카오톡 미설치")
        }
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

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setTopBarTitle("기록 상세보기")
    }
}