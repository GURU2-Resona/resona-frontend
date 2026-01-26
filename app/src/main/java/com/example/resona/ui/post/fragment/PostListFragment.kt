package com.example.resona.ui.post.fragment

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.resona.R
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.databinding.FragmentPostListBinding
import com.example.resona.ui.post.viewmodel.PostViewModel
import com.example.resona.ui.post.adapter.PostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostListFragment : Fragment(R.layout.fragment_post_list) {
    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    // 현재 선택된 필터 상태 관리
    private var selectedCategory: RecommendCategory? = null
    private var selectedScene: RecommendScene? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
        setupFilterListeners()

        // 화면 진입 시 초기 데이터 로드 (필터 없음)
        viewModel.loadPosts()
    }

    private fun setupRecyclerView() {
        // 화면 전환 없이 리스트만 출력하므로 클릭 리스너가 제외된 어댑터 생성
        postAdapter = PostAdapter(emptyList())
        binding.rvPostList.adapter = postAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // ViewModel의 UI 상태 관찰
            viewModel.uiState.collect { state ->
                // 데이터가 있을 때 어댑터 갱신
                if (state.posts.isNotEmpty()) {
                    postAdapter.updateData(state.posts)
                }

                // 로딩 및 에러 처리
                if (state.isLoading) {
                    // 로딩바 표시 로직
                }
                state.error?.let {
                    // 에러 메시지 표시 로직
                }
            }
        }
    }

    private fun setupFilterListeners() {
        // 1. 카테고리 필터 버튼 클릭
        binding.btnFilterCategory.setOnClickListener { view ->
            showFilterMenu(view, R.menu.menu_category) { selectedLabel ->
                // UI 텍스트 업데이트
                binding.tvCategoryLabel.text = selectedLabel

                // 한글 라벨을 기반으로 이넘 객체 찾기
                selectedCategory = if (selectedLabel == "전체") null else RecommendCategory.Companion.fromLabel(selectedLabel)

                updateList()
            }
        }

        // 2. 상황 필터 버튼 클릭
        binding.btnFilterSituation.setOnClickListener { view ->
            showFilterMenu(view, R.menu.menu_scene) { selectedLabel ->
                // UI 텍스트 업데이트
                binding.tvSituationLabel.text = selectedLabel

                // 한글 라벨을 기반으로 이넘 객체 찾기
                selectedScene = if (selectedLabel == "전체") null else RecommendScene.Companion.fromLabel(selectedLabel)

                updateList()
            }
        }
    }

    /**
     * PopupMenu를 생성하고 선택된 아이템의 타이틀을 콜백으로 반환
     */
    private fun showFilterMenu(anchor: View, menuRes: Int, onItemSelected: (String) -> Unit) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            onItemSelected(item.title.toString())
            true
        }
        popup.show()
    }

    /**
     * 현재 선택된 이넘 값들을 사용하여 API를 다시 호출
     */
    private fun updateList() {
        viewModel.loadPosts(selectedCategory, selectedScene)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}