//package com.example.resona.ui
//
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import com.example.resona.R
//import com.example.resona.databinding.FragmentPostListBinding
//
//class PostListFragment : Fragment(R.layout.fragment_post_list) {
//    private var _binding: FragmentPostListBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentPostListBinding.bind(view)
//
//        // 전달받은 타입 확인
//        val type = arguments?.getString("postType") ?: "MY"
//
//        // 타입에 따라 텍스트 변경 (TODO: 타입에 따라 다른 API 연동으로 변경 예정)
//        binding.tvTempTitle.text = when (type) {
//            "MY" -> "나의 추천글 화면입니다"
//            "SAVED" -> "저장한 추천글 화면입니다"
//            else -> "준비 중인 화면입니다 ($type)"
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

package com.example.resona.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resona.R
import com.example.resona.databinding.FragmentPostListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostListFragment : Fragment(R.layout.fragment_post_list) {
    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter

    // 전체 데이터와 현재 화면용 데이터
    private val allPosts = mutableListOf<PostModel>()
    private var currentTypePosts = listOf<PostModel>()

    // 필터 상태 (기본값)
    private var selectedCategory = "카테고리"
    private var selectedSituation = "상황"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        // 1. [기존 로직 유지] 전달받은 타입 확인
        val type = arguments?.getString("postType") ?: "MY"

        // 2. 더미 데이터 생성 (서버 연결 전까지 사용)
        generateDummyData()

        // 3. [기존 로직 확장] 타입에 따라 타이틀과 보여줄 데이터를 다르게 설정
        when (type) {
            "MY" -> {
                binding.tvPageTitle.text = "나의 추천글 보기"
                // '나의'가 포함된 데이터만 필터링 (예시 로직)
                currentTypePosts = allPosts.filter { it.title.contains("나의") }
            }
            "SAVED" -> {
                binding.tvPageTitle.text = "저장한 추천글 보기"
                // '저장'이 포함된 데이터만 필터링
                currentTypePosts = allPosts.filter { it.title.contains("저장") }
            }
            else -> {
                binding.tvPageTitle.text = "추천글 보기"
                currentTypePosts = allPosts
            }
        }

        // 4. UI 설정 (리스트 연결 및 필터 설정)
        setupRecyclerView(currentTypePosts)
        setupSpinners()
    }

    private fun setupRecyclerView(initialData: List<PostModel>) {
        postAdapter = PostAdapter(initialData)
        binding.rvPostList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun setupSpinners() {
        val categories = listOf("카테고리", "음악", "영상", "책", "ASMR")
        val situations = listOf("상황", "운동", "공부", "휴식", "새벽")

        // 기본 스피너 레이아웃 사용 (텍스트 색상은 테마에 따름)
        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        val sitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, situations)

        binding.spinnerCategory.adapter = catAdapter
        binding.spinnerSituation.adapter = sitAdapter

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent == binding.spinnerCategory) selectedCategory = categories[position]
                if (parent == binding.spinnerSituation) selectedSituation = situations[position]

                applyFilter() // 필터 적용
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerCategory.onItemSelectedListener = listener
        binding.spinnerSituation.onItemSelectedListener = listener
    }

    private fun applyFilter() {
        // 현재 페이지의 데이터(currentTypePosts) 안에서 카테고리/상황으로 2차 필터링
        val filtered = currentTypePosts.filter { post ->
            val matchCat = (selectedCategory == "카테고리") || (post.category == selectedCategory)
            val matchSit = (selectedSituation == "상황") || (post.situation == selectedSituation)
            matchCat && matchSit
        }
        postAdapter.updateData(filtered)
    }

    private fun generateDummyData() {
        allPosts.clear()
        val cats = listOf("음악", "영상", "책", "ASMR")
        val sits = listOf("운동", "공부", "휴식", "새벽")

        for (i in 1..30) {
            val c = cats.random()
            val s = sits.random()
            // 로직 테스트를 위해 제목에 태그를 넣음
            val prefix = if (i % 3 == 0) "[나의]" else if (i % 3 == 1) "[저장]" else "[추천]"

            allPosts.add(PostModel(
                title = "$prefix $c $i",
                subhead = "$s 할 때 좋은 콘텐츠",
                category = c,
                situation = s
            ))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}