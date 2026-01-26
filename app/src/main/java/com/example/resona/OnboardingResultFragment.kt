package com.example.resona

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.remote.model.OnboardingViewModel
import com.example.resona.databinding.FragmentOnboardingResultBinding
import com.example.resona.ui.main.MainActivity
import kotlin.getValue
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.resona.data.remote.model.NicknameViewModel
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
                                    binding.skeletonNickname.visibility = View.INVISIBLE
                                    binding.tvNickname.text = "${r.data} 님을 위한 노래입니다"
                                }

                                is ApiResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        r.exception.message ?: "닉네임을 불러올 수 없습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        onboardingViewModel.onboardingRecommendResult.observe(viewLifecycleOwner) { result ->
            _binding?.let { binding ->
                when (result) {
                    is ApiResult.Success -> {
                        val data = result.data
                        binding.skeletonTitle.visibility = View.INVISIBLE
                        binding.skeletonArtist.visibility = View.INVISIBLE
                        binding.tvSongTitle.text = data.title
                        binding.tvArtist.text = data.artist

                        if (data.youtubeUrl.equals("적합한 링크를 찾지 못했습니다.")){
                            Toast.makeText(
                                requireContext(),
                                 "음악 추천 실패",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        youtubeVideoId = data.youtubeUrl.split("=")[1]
                        Log.d("youtube", youtubeVideoId.toString())

                        binding.skeletonThumbnail.visibility = View.INVISIBLE
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
                    is ApiResult.Error -> {
                        Toast.makeText(
                            requireContext(),
                            result.exception.message ?: "음악 추천 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        val homeButton = view.findViewById<Button>(R.id.btn_home);
        homeButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
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