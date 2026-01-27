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

        val postType = arguments?.getString("postType")
        val targetId = arguments?.getLong("targetMemberId", -1L) ?: -1L

        // 분기 처리에 "my" 케이스 추가
        when (postType) {
            "other" -> {
                if (targetId != -1L) viewModel.loadOtherMemberPosts(targetId, selectedCategory, selectedScene)
            }
            "saved" -> {
                viewModel.loadScrappedPosts(selectedCategory, selectedScene)
            }
            "my" -> {
                // 마이페이지 -> 내 추천글 조회인 경우
                viewModel.loadMyPosts(selectedCategory, selectedScene)
            }
            else -> {
                viewModel.loadPosts(selectedCategory, selectedScene)
            }
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList()).apply {
            onItemClick = { post ->
                val bundle = Bundle().apply {
                    putLong("postId", post.postId)
                }
                findNavController().navigate(R.id.navigation_post_detail, bundle)
            }
        }
        binding.rvPostList.adapter = postAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
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

    // 필터링 변경 시에도 postType을 체크
    private fun updateList() {
        val postType = arguments?.getString("postType")
        val targetId = arguments?.getLong("targetMemberId", -1L) ?: -1L

        when (postType) {
            "other" -> {
                if (targetId != -1L) viewModel.loadOtherMemberPosts(targetId, selectedCategory, selectedScene)
            }
            "saved" -> {
                viewModel.loadScrappedPosts(selectedCategory, selectedScene)
            }
            "my" -> {
                viewModel.loadMyPosts(selectedCategory, selectedScene)
            }
            else -> {
                viewModel.loadPosts(selectedCategory, selectedScene)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}