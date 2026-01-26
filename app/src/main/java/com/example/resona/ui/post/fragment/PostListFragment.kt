package com.example.resona.ui.post.fragment

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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

    private var selectedCategory: RecommendCategory? = null
    private var selectedScene: RecommendScene? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
        setupFilterListeners()

        // [추가] 전달받은 인자값에 따른 데이터 로드 분기
        val postType = arguments?.getString("postType")
        val targetId = arguments?.getLong("targetMemberId", -1L) ?: -1L

        if (postType == "other" && targetId != -1L) {
            // 1. 타인 프로필에서 넘어온 경우: 해당 사용자의 게시글만 로드
            viewModel.loadOtherMemberPosts(targetId)
        } else {
            // 2. 일반적인 경우: 전체 게시글 로드
            viewModel.loadPosts(selectedCategory, selectedScene)
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList()).apply {
            onItemClick = { post ->
                val bundle = Bundle().apply {
                    putLong("postId", post.postId.toLong())
                }
                findNavController().navigate(R.id.navigation_post_detail, bundle)
            }
        }
        binding.rvPostList.adapter = postAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // API 결과(state.posts)가 어댑터에 전달됨
                postAdapter.updateData(state.posts)
            }
        }
    }

    private fun setupFilterListeners() {
        binding.btnFilterCategory.setOnClickListener { view ->
            showFilterMenu(view, R.menu.menu_category) { selectedLabel ->
                binding.tvCategoryLabel.text = selectedLabel
                selectedCategory = if (selectedLabel == "전체") null else RecommendCategory.fromLabel(selectedLabel)
                updateList()
            }
        }

        binding.btnFilterSituation.setOnClickListener { view ->
            showFilterMenu(view, R.menu.menu_scene) { selectedLabel ->
                binding.tvSituationLabel.text = selectedLabel
                selectedScene = if (selectedLabel == "전체") null else RecommendScene.fromLabel(selectedLabel)
                updateList()
            }
        }
    }

    private fun showFilterMenu(anchor: View, menuRes: Int, onItemSelected: (String) -> Unit) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            onItemSelected(item.title.toString())
            true
        }
        popup.show()
    }

    private fun updateList() {
        val postType = arguments?.getString("postType")
        val targetId = arguments?.getLong("targetMemberId", -1L) ?: -1L

        if (postType == "other" && targetId != -1L) {
            viewModel.loadOtherMemberPosts(targetId, selectedCategory, selectedScene)
        } else {
            // 일반 게시글 목록 조회
            viewModel.loadPosts(selectedCategory, selectedScene)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}