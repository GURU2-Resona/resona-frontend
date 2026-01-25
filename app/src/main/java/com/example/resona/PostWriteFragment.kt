package com.example.resona

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch

class PostWriteFragment : Fragment() {

    private var currentVideoId: String? = null
    private var currentSongTitle: String = ""
    private var currentSinger: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch = view.findViewById<EditText>(R.id.et_write_search)
        val btnSearch = view.findViewById<ImageView>(R.id.iv_write_search_btn)
        val youtubePlayerView = view.findViewById<YouTubePlayerView>(R.id.youtube_player_view)
        val ivLogo = view.findViewById<ImageView>(R.id.iv_write_logo)
        val etSubject = view.findViewById<EditText>(R.id.et_write_subject)
        val etContent = view.findViewById<EditText>(R.id.et_write_content)
        val nextButton = view.findViewById<Button>(R.id.btn_write_next)

        viewLifecycleOwner.lifecycle.addObserver(youtubePlayerView)

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                lifecycleScope.launch {
                    val videoId = YoutubeSearchManager.getFirstVideoId(query, "")
                    if (videoId != null) {
                        currentVideoId = videoId
                        currentSongTitle = query
                        currentSinger = "Unknown"

                        ivLogo.visibility = View.GONE
                        youtubePlayerView.visibility = View.VISIBLE

                        youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })
                    } else {
                        Toast.makeText(context, "검색 결과를 찾을 수 없습니다. ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val subject = etSubject.text.toString().trim()
                val content = etContent.text.toString().trim()
                val isActive = subject.isNotEmpty() && content.isNotEmpty()
                nextButton.isEnabled = isActive
                nextButton.setTextColor(if (isActive) Color.WHITE else Color.parseColor("#6581FF"))
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etSubject.addTextChangedListener(textWatcher)
        etContent.addTextChangedListener(textWatcher)

        nextButton.setOnClickListener {
            val subject = etSubject.text.toString()
            val content = etContent.text.toString()
            val bundle = Bundle().apply {
                putString("userSubject", subject)
                putString("userContent", content)
                putString("videoId", currentVideoId)
                putString("songTitle", currentSongTitle)
                putString("singer", currentSinger)
            }
            findNavController().navigate(R.id.action_postWrite_to_postCategory, bundle)
        }
    }
}