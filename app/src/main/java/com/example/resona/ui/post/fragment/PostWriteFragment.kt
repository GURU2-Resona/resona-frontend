package com.example.resona.ui.post.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.YoutubeSearchManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostWriteFragment : Fragment(R.layout.fragment_post_write) {

    private var currentVideoId: String? = null
    private var currentSongTitle: String = ""
    private var currentSinger: String = ""
    private var currentThumbnailUrl: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val etSearch = view.findViewById<EditText>(R.id.et_write_search)
        val btnSearch = view.findViewById<ImageView>(R.id.iv_write_search_btn)
        val ivThumbnail = view.findViewById<ImageView>(R.id.youtube_player_view)
        val ivLogo = view.findViewById<ImageView>(R.id.iv_write_logo)
        val etSubject = view.findViewById<EditText>(R.id.et_write_subject)
        val etContent = view.findViewById<EditText>(R.id.et_write_content)
        val nextButton = view.findViewById<Button>(R.id.btn_write_next)

        btnSearch.setOnClickListener {
            performSearch(etSearch.text.toString().trim(), ivLogo, ivThumbnail, etSubject, etContent, nextButton)
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.text.toString().trim(), ivLogo, ivThumbnail, etSubject, etContent, nextButton)
                true
            } else false
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkNextButtonState(etSubject, etContent, nextButton)
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        etSubject.addTextChangedListener(textWatcher)
        etContent.addTextChangedListener(textWatcher)

        nextButton.setOnClickListener {
            if (currentVideoId == null) {
                Toast.makeText(context, "노래를 먼저 검색해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply {
                putString("userSubject", etSubject.text.toString())
                putString("userContent", etContent.text.toString())
                putString("videoId", currentVideoId)
                putString("songTitle", currentSongTitle)
                putString("singer", currentSinger)
            }
            findNavController().navigate(R.id.action_postWrite_to_postCategory, bundle)
        }
    }

    private fun performSearch(query: String, ivLogo: View, ivThumbnail: ImageView, etSub: EditText, etCon: EditText, btnNext: Button) {
        if (query.isNotEmpty()) {
            lifecycleScope.launch {
                val results = YoutubeSearchManager.searchVideos(query)
                if (results.isNotEmpty()) {
                    val bottomSheet = YoutubeSearchBottomSheet(results) { selected ->
                        currentVideoId = selected.videoId
                        currentSongTitle = selected.title
                        currentSinger = selected.channelTitle
                        currentThumbnailUrl = selected.thumbnailUrl

                        ivLogo.visibility = View.GONE
                        ivThumbnail.visibility = View.VISIBLE

                        // Glide로 썸네일 이미지 로드
                        Glide.with(ivThumbnail)
                            .load(selected.thumbnailUrl)
                            .into(ivThumbnail)

                        checkNextButtonState(etSub, etCon, btnNext)
                    }
                    bottomSheet.show(childFragmentManager, "YoutubeSearch")
                } else {
                    Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkNextButtonState(etSubject: EditText, etContent: EditText, nextButton: Button) {
        val subject = etSubject.text.toString().trim()
        val content = etContent.text.toString().trim()
        val isActive = subject.isNotEmpty() && content.isNotEmpty() && currentVideoId != null
        nextButton.isEnabled = isActive
        nextButton.setTextColor(if (isActive) Color.WHITE else Color.parseColor("#6581FF"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}