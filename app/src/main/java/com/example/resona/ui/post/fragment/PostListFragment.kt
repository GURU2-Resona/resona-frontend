//package com.example.resona.ui.post.fragment
//
//import android.os.Bundle
//import android.view.View
//import android.widget.PopupMenu
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import com.example.resona.R
//import com.example.resona.data.enums.RecommendCategory
//import com.example.resona.data.enums.RecommendScene
//import com.example.resona.databinding.FragmentPostListBinding
//import com.example.resona.ui.main.MainActivity
//import com.example.resona.ui.post.viewmodel.PostViewModel
//import com.example.resona.ui.post.adapter.PostAdapter
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//
//@AndroidEntryPoint
//class PostListFragment : Fragment(R.layout.fragment_post_list) {
//    private var _binding: FragmentPostListBinding? = null
//    private val binding get() = _binding!!
//
//    private val viewModel: PostViewModel by viewModels()
//    private lateinit var postAdapter: PostAdapter
//
//    private var selectedCategory: RecommendCategory? = null
//    private var selectedScene: RecommendScene? = null
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        onResume()
//        _binding = FragmentPostListBinding.bind(view)
//
//        setupRecyclerView()
//        observeViewModel()
//        setupFilterListeners()
//
//        val postType = arguments?.getString("postType")
//        val targetId = arguments?.getLong("targetMemberId", -1L) ?: -1L
//
//        // 분기 처리에 "my" 케이스 추가
//        when (postType) {
//            "other" -> {
//                if (targetId != -1L) viewModel.loadOtherMemberPosts(targetId, selectedCategory, selectedScene)
//            }
//            "saved" -> {
//                viewModel.loadScrappedPosts(selectedCategory, selectedScene)
//            }
//            "my" -> {
//                // 마이페이지 -> 내 추천글 조회인 경우
//                viewModel.loadMyPosts(selectedCategory, selectedScene)
//            }
//            else -> {
//                viewModel.loadPosts(selectedCategory, selectedScene)
//            }
//        }
//    }
//
//    private fun setupRecyclerView() {
//        postAdapter = PostAdapter(emptyList()).apply {
//            onItemClick = { post ->
//                val bundle = Bundle().apply {
//                    putLong("postId", post.postId)
//                }
//                findNavController().navigate(R.id.navigation_post_detail, bundle)
//            }
//        }
//        binding.rvPostList.adapter = postAdapter
//    }
//
//    private fun observeViewModel() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.uiState.collect { state ->
//                postAdapter.updateData(state.posts)
//            }
//        }
//    }
//
//    private fun setupFilterListeners() {
//        binding.btnFilterCategory.setOnClickListener { view ->
//            showFilterMenu(view, R.menu.menu_category) { selectedLabel ->
//                binding.tvCategoryLabel.text = selectedLabel
//                selectedCategory = if (selectedLabel == "전체") null else RecommendCategory.fromLabel(selectedLabel)
//                updateList()
//            }
//        }
//
//        binding.btnFilterSituation.setOnClickListener { view ->
//            showFilterMenu(view, R.menu.menu_scene) { selectedLabel ->
//                binding.tvSituationLabel.text = selectedLabel
//                selectedScene = if (selectedLabel == "전체") null else RecommendScene.fromLabel(selectedLabel)
//                updateList()
//            }
//        }
//    }
//
//    private fun showFilterMenu(anchor: View, menuRes: Int, onItemSelected: (String) -> Unit) {
//        val popup = PopupMenu(requireContext(), anchor)
//        popup.menuInflater.inflate(menuRes, popup.menu)
//        popup.setOnMenuItemClickListener { item ->
//            onItemSelected(item.title.toString())
//            true
//        }
//        popup.show()
//    }
//
//    // 필터링 변경 시에도 postType을 체크
//    private fun updateList() {
//        val postType = arguments?.getString("postType")
//        val targetId = arguments?.getLong("targetMemberId", -1L) ?: -1L
//
//        when (postType) {
//            "other" -> {
//                if (targetId != -1L) viewModel.loadOtherMemberPosts(targetId, selectedCategory, selectedScene)
//            }
//            "saved" -> {
//                viewModel.loadScrappedPosts(selectedCategory, selectedScene)
//            }
//            "my" -> {
//                viewModel.loadMyPosts(selectedCategory, selectedScene)
//            }
//            else -> {
//                viewModel.loadPosts(selectedCategory, selectedScene)
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    override fun onResume() {
//        super.onResume()
//        val postType = arguments?.getString("postType")
//        val topbarTitle = when (postType) {
//            "other" -> "기록 모아보기"
//            "saved" -> "저장한 기록보기"
//            "my" -> "나의 기록보기"
//            "all" -> "전체 기록보기"
//            else -> "전체 기록보기"
//        }
//        (activity as? MainActivity)?.setTopBarTitle(topbarTitle)
//    }
//}
package com.example.resona.ui.post.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.resona.R
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.databinding.FragmentPostListBinding
import com.example.resona.ui.main.MainActivity
import com.example.resona.ui.post.adapter.PostAdapter
import com.example.resona.ui.post.viewmodel.PostViewModel
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
        onResume()
        _binding = FragmentPostListBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
        setupFilterListeners()

        loadInitialData()
    }

    private fun loadInitialData() {
        val postType = arguments?.getString("postType")
        val targetId = arguments?.getLong("targetMemberId", -1L) ?: -1L
        fetchPosts(postType, targetId)
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList()).apply {
            onItemClick = { post ->
                val bundle = Bundle().apply { putLong("postId", post.postId) }
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
        // 1. 카테고리 필터 데이터 준비 (전체 + Enum 라벨들)
        val categoryItems = listOf("전체") + RecommendCategory.entries.map { it.label }

        binding.btnFilterCategory.setOnClickListener { view ->
            showDropdownMenu(
                anchor = view,
                arrowView = binding.ivCategoryArrow,
                items = categoryItems
            ) { selectedLabel ->
                binding.tvCategoryLabel.text = selectedLabel
                selectedCategory = if (selectedLabel == "전체") null else RecommendCategory.fromLabel(selectedLabel)
                updateList()
            }
        }

        // 2. 상황 필터 데이터 준비
        val sceneItems = listOf("전체") + RecommendScene.entries.map { it.label }

        binding.btnFilterSituation.setOnClickListener { view ->
            showDropdownMenu(
                anchor = view,
                arrowView = binding.ivSituationArrow,
                items = sceneItems
            ) { selectedLabel ->
                binding.tvSituationLabel.text = selectedLabel
                selectedScene = if (selectedLabel == "전체") null else RecommendScene.fromLabel(selectedLabel)
                updateList()
            }
        }
    }

    // [핵심 변경] ListPopupWindow를 사용하여 너비와 디자인 제어
    private fun showDropdownMenu(
        anchor: View,
        arrowView: ImageView,
        items: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val listPopupWindow = ListPopupWindow(requireContext())
        listPopupWindow.anchorView = anchor

        // 1. 너비 맞춤: 앵커(칩 버튼)의 너비와 동일하게 설정
        listPopupWindow.width = anchor.width

        // 2. 둥근 배경 적용 (기존에 만드신 drawable 사용)
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_popup_menu))

        // 3. 어댑터 설정 (커스텀 레이아웃 사용)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_filter_dropdown, items)
        listPopupWindow.setAdapter(adapter)

        // 4. 위치 미세 조정 (칩 바로 아래에 붙도록)
        listPopupWindow.verticalOffset = 0 // 필요하면 8dp 등으로 조정 가능
        listPopupWindow.isModal = true

        // 화살표 회전 애니메이션
        arrowView.animate().rotation(180f).setDuration(200).start()

        listPopupWindow.setOnItemClickListener { _, _, position, _ ->
            onItemSelected(items[position])
            listPopupWindow.dismiss()
        }

        listPopupWindow.setOnDismissListener {
            arrowView.animate().rotation(0f).setDuration(200).start()
        }

        listPopupWindow.show()

        // [구분선 추가] 팝업이 보여진 후 ListView에 접근하여 구분선 설정
        listPopupWindow.listView?.let { listView ->
            listView.divider = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.neutral_400))
            listView.dividerHeight = 1 // 1px 높이
        }
    }

    private fun updateList() {
        val postType = arguments?.getString("postType")
        val targetId = arguments?.getLong("targetMemberId", -1L) ?: -1L
        fetchPosts(postType, targetId)
    }

    private fun fetchPosts(postType: String?, targetId: Long) {
        when (postType) {
            "other" -> if (targetId != -1L) viewModel.loadOtherMemberPosts(targetId, selectedCategory, selectedScene)
            "saved" -> viewModel.loadScrappedPosts(selectedCategory, selectedScene)
            "my" -> viewModel.loadMyPosts(selectedCategory, selectedScene)
            else -> viewModel.loadPosts(selectedCategory, selectedScene)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val postType = arguments?.getString("postType")
        val topbarTitle = when (postType) {
            "other" -> "기록 모아보기"
            "saved" -> "저장한 기록보기"
            "my" -> "나의 기록보기"
            else -> "전체 기록보기"
        }
        (activity as? MainActivity)?.setTopBarTitle(topbarTitle)
    }
}
