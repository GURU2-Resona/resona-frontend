//package com.example.resona.ui
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.resona.R
//
//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [HomeFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
//class HomeFragment : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment HomeFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}

package com.example.resona.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // 온보딩에서 사용했던 것과 동일한 유튜브 ID (예시)
    private val youtubeVideoId = "hrXCP0xeoA8"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // 1. 오늘의 음악 추천 (온보딩 로직 재사용)
        setupMusicCard()

        // 2. 추천글 보기 (화살표) 클릭 이벤트
        binding.btn_go_post_list.setOnClickListener {
            // 네비게이션 그래프에 정의된 ID로 이동
            findNavController().navigate(R.id.navigation_post_list)
        }

        // 3. 추천글 목록 미리보기 (3개만 표시)
        setupPreviewList()
    }

    private fun setupMusicCard() {
        // 썸네일 로드 (Glide)
        val thumbnailUrl = "https://img.youtube.com/vi/$youtubeVideoId/0.jpg"
        Glide.with(this)
            .load(thumbnailUrl)
            .placeholder(R.color.neutral_400) // 로딩 중 색상
            .into(binding.ivMusicThumbnail)

        // 카드 클릭 시 유튜브 실행 (Intent)
        binding.cardMusic.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=$youtubeVideoId")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // 텍스트 설정 (필요 시 동적 변경 가능)
        binding.tvMusicName.text = "행운을 빌어줘"
        binding.tvMusicArtist.text = "원필"
    }

    private fun setupPreviewList() {
        // 더미 데이터 생성 (PostListFragment 로직 참고)
        val dummyData = generateDummyData()

        // 상위 3개만 자르기
        val previewData = dummyData.take(3)

        // 어댑터 연결 (기존 PostAdapter 재사용)
        val adapter = PostAdapter(previewData)

        binding.rvHomePreview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            // 스크롤 중첩 방지 (홈 화면 자체는 스크롤 없다고 하셨으나,
            // 혹시 내용이 길어질 경우 리사이클러뷰 자체 스크롤은 유지)
            isNestedScrollingEnabled = true
        }
    }

    private fun generateDummyData(): List<PostModel> {
        // PostListFragment와 동일한 더미 데이터 생성 로직
        val list = mutableListOf<PostModel>()
        val cats = listOf("음악", "영상", "책", "ASMR")
        val sits = listOf("운동", "공부", "휴식", "새벽")

        for (i in 1..10) {
            val c = cats.random()
            val s = sits.random()
            val prefix = "[추천]" // 홈 화면에서는 일반 추천글 위주로 표시

            list.add(PostModel(
                title = "$prefix $c $i",
                subhead = "$s 할 때 좋은 콘텐츠",
                category = c,
                situation = s
            ))
        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}