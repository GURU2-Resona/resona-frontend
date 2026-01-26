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
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE
        postId = arguments?.getLong("postId") ?: -1L
        if (postId != -1L) loadPostDetail()
    }

    private fun loadPostDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = viewModel.repository.getPostDetail(postId)
            if (result is ApiResult.Success) {
                val data = result.data
                if (data.isMine) navigateToShare(data)
                else bindDataToUI(data)
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
            tvDetailSongTitle.text = data.title
            tvDetailMainText.text = data.content
            tvDetailNickname.text = data.writerNickname
            tvDetailHash.text = "#${data.categoryName} #${data.sceneName}"

            Glide.with(this@PostDetailFragment)
                .load(data.writerProfileImage)
                .placeholder(R.drawable.ic_placeholder)
                .circleCrop()
                .into(ivProfile)

            val videoId = extractVideoId(data.songUrl)
            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"

            Glide.with(this@PostDetailFragment)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_thumnail_placeholder)
                .centerCrop()
                .into(ivDetailAlbumArt)

            isSaved = data.isSaved
            ivSave.setImageResource(if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark)
            ivSave.setOnClickListener { toggleBookmark() }

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
                Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
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