package com.example.resona.ui

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
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

    // 현재 선택된 필터 (초기값: 전체)
    // 화면상의 텍스트는 "카테고리", "상황"으로 유지하되, 내부 로직은 "전체"로 처리
    private var selectedCategory = "전체"
    private var selectedSituation = "전체"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        // 1. 전달받은 타입 확인 (NavArgument)
        val type = arguments?.getString("postType") ?: "MY"

        // 2. 더미 데이터 생성
        generateDummyData()

        // 3. 타입에 따른 초기 설정
        when (type) {
            "MY" -> {
                binding.tvPageTitle.text = "나의 추천글 보기"
                currentTypePosts = allPosts.filter { it.title.contains("나의") }
            }
            "SAVED" -> {
                binding.tvPageTitle.text = "저장한 추천글 보기"
                currentTypePosts = allPosts.filter { it.title.contains("저장") }
            }
            else -> {
                binding.tvPageTitle.text = "추천글 보기"
                currentTypePosts = allPosts
            }
        }

        // 4. RecyclerView 설정
        setupRecyclerView(currentTypePosts)

        // 5. 필터 버튼 설정 (드롭다운 연결)
        setupFilterButtons()
    }

    private fun setupRecyclerView(initialData: List<PostModel>) {
        postAdapter = PostAdapter(initialData)
        binding.rvPostList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun setupFilterButtons() {
        // "전체"는 필터 해제용으로 포함하거나 제외 가능
        val categories = listOf("전체", "음악", "영상", "책", "ASMR", "팟캐스트", "기타")
        val situations = listOf("전체", "운동", "공부", "휴식", "새벽", "출근", "퇴근")

        // 카테고리 버튼 클릭 시
        binding.btnFilterCategory.setOnClickListener { view ->
            showDropdownMenu(view, categories) { selectedItem ->
                selectedCategory = selectedItem
                // UI 텍스트 업데이트 (선택된 항목으로 변경)
                // 만약 "전체" 선택 시 다시 "카테고리"로 되돌리고 싶다면 분기 처리
                binding.tvCategoryLabel.text = if (selectedItem == "전체") "카테고리" else selectedItem
                applyFilter()
            }
        }

        // 상황 버튼 클릭 시
        binding.btnFilterSituation.setOnClickListener { view ->
            showDropdownMenu(view, situations) { selectedItem ->
                selectedSituation = selectedItem
                binding.tvSituationLabel.text = if (selectedItem == "전체") "상황" else selectedItem
                applyFilter()
            }
        }
    }

    private fun showDropdownMenu(anchor: View, items: List<String>, onItemSelected: (String) -> Unit) {
        val popup = PopupMenu(requireContext(), anchor)

        // 메뉴 아이템 추가
        items.forEach { item ->
            popup.menu.add(item)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            onItemSelected(menuItem.title.toString())
            true
        }
        popup.show()
    }

    private fun applyFilter() {
        val filtered = currentTypePosts.filter { post ->
            val matchCat = (selectedCategory == "전체") || (post.category == selectedCategory)
            val matchSit = (selectedSituation == "전체") || (post.situation == selectedSituation)
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