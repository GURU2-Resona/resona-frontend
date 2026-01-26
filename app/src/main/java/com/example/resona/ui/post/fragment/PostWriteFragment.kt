package com.example.resona.ui.post.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.YoutubeSearchManager
import com.example.resona.databinding.FragmentPostWriteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostWriteFragment : Fragment(R.layout.fragment_post_write) {

    private var _binding: FragmentPostWriteBinding? = null
    private val binding get() = _binding!!

    private var currentVideoId: String? = null
    private var currentSongTitle: String = ""
    private var currentSinger: String = ""
    private var currentThumbnailUrl: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostWriteBinding.bind(view)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // 검색 버튼 클릭 리스너
        binding.ivWriteSearchBtn.setOnClickListener {
            performSearch(binding.etWriteSearch.text.toString().trim())
        }

        // 키보드 검색 액션 리스너
        binding.etWriteSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.etWriteSearch.text.toString().trim())
                true
            } else false
        }

        // 텍스트 감시자 설정
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etWriteSubject.addTextChangedListener(textWatcher)
        binding.etWriteContent.addTextChangedListener(textWatcher)

        // 다음 버튼 클릭 리스너
        binding.btnWriteNext.setOnClickListener {
            if (currentVideoId == null) {
                Toast.makeText(context, "노래를 먼저 검색해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply {
                putString("userSubject", binding.etWriteSubject.text.toString())
                putString("userContent", binding.etWriteContent.text.toString())
                putString("videoId", currentVideoId)
                putString("songTitle", currentSongTitle)
                putString("singer", currentSinger)
            }
            findNavController().navigate(R.id.action_postWrite_to_postCategory, bundle)
        }
    }

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            lifecycleScope.launch {
                val results = YoutubeSearchManager.searchVideos(query)
                if (results.isNotEmpty()) {
                    val bottomSheet = YoutubeSearchBottomSheet(results) { selected ->
                        currentVideoId = selected.videoId
                        currentSongTitle = selected.title
                        currentSinger = selected.channelTitle
                        currentThumbnailUrl = selected.thumbnailUrl

                        binding.ivWriteLogo.visibility = View.GONE
                        binding.youtubePlayerView.visibility = View.VISIBLE

                        // Glide로 썸네일 이미지 로드
                        Glide.with(this@PostWriteFragment)
                            .load(selected.thumbnailUrl)
                            .into(binding.youtubePlayerView)

                        checkNextButtonState()
                    }
                    bottomSheet.show(childFragmentManager, "YoutubeSearch")
                } else {
                    Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkNextButtonState() {
        val subject = binding.etWriteSubject.text.toString().trim()
        val content = binding.etWriteContent.text.toString().trim()
        val isActive = subject.isNotEmpty() && content.isNotEmpty() && currentVideoId != null

        binding.btnWriteNext.apply {
            isEnabled = isActive
            setTextColor(if (isActive) Color.WHITE else Color.parseColor("#6581FF"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        _binding = null
    }
}