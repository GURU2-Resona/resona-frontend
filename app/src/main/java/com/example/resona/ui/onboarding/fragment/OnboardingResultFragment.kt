package com.example.resona.ui.onboarding.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import androidx.navigation.NavOptions
import com.example.resona.R
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.ui.onboarding.viewmodel.NicknameViewModel
import com.example.resona.ui.onboarding.viewmodel.OnboardingViewModel
import com.example.resona.databinding.FragmentOnboardingResultBinding
import com.example.resona.ui.main.MainActivity
import kotlinx.coroutines.launch

class OnboardingResultFragment : Fragment(R.layout.fragment_onboarding_result) {
    private var _binding: FragmentOnboardingResultBinding? = null
    private val binding get() = _binding!!
    private var youtubeVideoId: String? = null
    private val onboardingViewModel : OnboardingViewModel by activityViewModels()
    private val nicknameViewModel : NicknameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingResultBinding.bind(view)
        (activity as? MainActivity)?.setTopBarTitle("음악 추천")

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                nicknameViewModel.nicknameSaveResult.collect { result ->
                    result?.let { r ->
                        _binding?.let { binding ->
                            when (r) {
                                is ApiResult.Success -> {
                                    binding.tvNickname.text = "${r.data} 님을 위한 노래입니다"
                                }
                                is ApiResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        r.exception.message ?: "닉네임을 불러올 수 없습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                is ApiResult.Loading -> {
                                }
                            }
                        }
                    }
                }
            }
        }

        onboardingViewModel.onboardingRecommendResult.observe(viewLifecycleOwner) { result ->
            if (result is ApiResult.Success) {
                val data = result.data
                binding.tvSongTitle.text = data.title
                binding.tvArtist.text = data.artist
                youtubeVideoId = data.youtubeUrl.split("=")[1]
                val thumbnailUrl = "https://img.youtube.com/vi/$youtubeVideoId/0.jpg"
                Glide.with(this)
                    .load(thumbnailUrl)
                    .placeholder(R.color.neutral_400)
                    .into(binding.ivThumbnail)

                binding.ivThumbnail.setOnClickListener {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.youtube.com/watch?v=$youtubeVideoId".toUri()
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }

        val homeButton = view.findViewById<Button>(R.id.btn_home);
        homeButton.setOnClickListener {
            // 홈으로 이동 시 Back Stack 삭제
            findNavController().navigate(
                R.id.navigation_home,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true) // 전체 기록 삭제
                    .build()
            )
        }

        val listenButton = view.findViewById<Button>(R.id.btn_listen_full);
        listenButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                "https://www.youtube.com/watch?v=$youtubeVideoId".toUri()
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