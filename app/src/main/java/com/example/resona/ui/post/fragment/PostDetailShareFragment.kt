package com.example.resona.ui.post.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.data.dto.PostDetailResponseDto
import com.example.resona.databinding.FragmentPostDetailShareBinding
import com.example.resona.ui.post.viewmodel.PostViewModel
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

            btnDetailShare.setOnClickListener { sharePostWithDomain(data) }

            btnListenAll.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.songUrl)))
            }
        }
    }

    private fun sharePostWithDomain(data: PostDetailResponseDto) {
        val appDeepLink = "https://resona-guru.store/post/${data.postId}"

        val shareMessage = """
            [Gong Myung] '${data.writerNickname}'님이 추천하는 음악입니다!
            
            제목: ${data.title}
            내용: ${data.content}
            
            👇 앱에서 자세히 보기 (딥링크)
            $appDeepLink
            
            📺 유튜브에서 듣기
            ${data.songUrl}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "음악 기록 공유")
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }

        startActivity(Intent.createChooser(intent, "공유하기"))
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