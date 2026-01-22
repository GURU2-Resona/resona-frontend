package com.example.resona

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class PostDetailFragment : Fragment() {

    private var isBookmarked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ID를 bottom_navigation에서 bottom_nav로 수정했습니다.
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.visibility = View.GONE

        val tvTitle = view.findViewById<TextView>(R.id.tv_detail_song_title)
        val tvMainText = view.findViewById<TextView>(R.id.tv_detail_main_text)
        val tvHash = view.findViewById<TextView>(R.id.tv_detail_hash)
        val ivBookmark = view.findViewById<ImageView>(R.id.iv_detail_bookmark)

        val title = arguments?.getString("finalSubject")
        val content = arguments?.getString("finalContent")
        val tags = arguments?.getString("finalTag")

        tvTitle.text = title ?: "입력된 제목이 없습니다"
        tvMainText.text = content ?: "입력된 내용이 없습니다"
        tvHash.text = tags ?: "#카테고리미정"

        ivBookmark.setOnClickListener {
            isBookmarked = !isBookmarked
            if (isBookmarked) {
                ivBookmark.setImageResource(R.drawable.ic_bookmark_filled)
            } else {
                ivBookmark.setImageResource(R.drawable.ic_bookmark)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 여기서도 ID를 bottom_nav로 수정했습니다.
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.visibility = View.VISIBLE
    }
}