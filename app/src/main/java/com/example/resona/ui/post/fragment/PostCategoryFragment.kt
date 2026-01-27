package com.example.resona.ui.post.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.resona.R
import com.example.resona.data.dto.PostCreateRequestDto
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.databinding.FragmentPostCategoryBinding
import com.example.resona.ui.main.MainActivity
import com.example.resona.ui.post.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostCategoryFragment : Fragment() {

    private var _binding: FragmentPostCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? MainActivity)?.setTopBarTitle("기록하기")
        _binding = FragmentPostCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달받은 데이터 추출
        val subject = arguments?.getString("userSubject") ?: ""
        val content = arguments?.getString("userContent") ?: ""
        val videoId = arguments?.getString("videoId") ?: ""
        val songTitle = arguments?.getString("songTitle") ?: ""

        // 1. UI 컴포넌트 리스트화 (바인딩 사용)
        val categoryButtons = listOf(
            binding.btnCategory1, binding.btnCategory2, binding.btnCategory3,
            binding.btnCategory4, binding.btnCategory5, binding.btnCategory6
        )
        val sceneButtons = listOf(
            binding.btnScene1, binding.btnScene2, binding.btnScene3,
            binding.btnScene4, binding.btnScene5, binding.btnScene6
        )

        // 2. 선택 로직 설정
        setupSelectionLogic(categoryButtons, binding.etCategoryDirect, sceneButtons, binding.etSceneDirect)

        // 3. 게시글 생성 버튼 클릭 리스너
        binding.btnCategoryNext.setOnClickListener {
            val category = if (binding.etCategoryDirect.text.isNotEmpty()) {
                RecommendCategory.OTHER
            } else {
                val index = categoryButtons.indexOfFirst { it.isSelected }
                if (index != -1) RecommendCategory.entries[index] else RecommendCategory.OTHER
            }

            val scene = if (binding.etSceneDirect.text.isNotEmpty()) {
                RecommendScene.OTHER
            } else {
                val index = sceneButtons.indexOfFirst { it.isSelected }
                if (index != -1) RecommendScene.entries[index] else RecommendScene.OTHER
            }

            val requestBody = PostCreateRequestDto(
                songTitle = songTitle,
                songUrl = "https://www.youtube.com/watch?v=$videoId",
                title = subject,
                content = content,
                category = category,
                customCategory = if (category == RecommendCategory.OTHER) binding.etCategoryDirect.text.toString() else null,
                scene = scene,
                customScene = if (scene == RecommendScene.OTHER) binding.etSceneDirect.text.toString() else null
            )

            viewLifecycleOwner.lifecycleScope.launch {
                val result = viewModel.repository.createPost(requestBody)
                when (result) {
                    is ApiResult.Success -> {
                        Toast.makeText(context, "추천글 등록 성공", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.navigation_home)
                    }
                    is ApiResult.Error -> {
                        Log.e("API_ERROR", "등록 실패: ${result.exception.message}")
                        Toast.makeText(context, "등록 실패: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupSelectionLogic(catBtns: List<Button>, catEt: EditText, sceBtns: List<Button>, sceEt: EditText) {
        // 카테고리 버튼 단일 선택
        catBtns.forEach { btn ->
            btn.setOnClickListener {
                catBtns.forEach { it.isSelected = false }
                catEt.text.clear()
                btn.isSelected = true
                checkNextButtonState(catBtns, catEt, sceBtns, sceEt)
            }
        }

        // 상황 버튼 단일 선택
        sceBtns.forEach { btn ->
            btn.setOnClickListener {
                sceBtns.forEach { it.isSelected = false }
                sceEt.text.clear()
                btn.isSelected = true
                checkNextButtonState(catBtns, catEt, sceBtns, sceEt)
            }
        }

        // 직접 입력 텍스트 와처
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    // 텍스트 입력 시 해당 그룹 버튼 선택 해제
                    if (catEt.hasFocus()) catBtns.forEach { it.isSelected = false }
                    if (sceEt.hasFocus()) sceBtns.forEach { it.isSelected = false }
                }
                checkNextButtonState(catBtns, catEt, sceBtns, sceEt)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        catEt.addTextChangedListener(watcher)
        sceEt.addTextChangedListener(watcher)
    }

    private fun checkNextButtonState(catBtns: List<Button>, catEt: EditText, sceBtns: List<Button>, sceEt: EditText) {
        val isActive = (catBtns.any { it.isSelected } || catEt.text.isNotEmpty()) &&
                (sceBtns.any { it.isSelected } || sceEt.text.isNotEmpty())

        with(binding.btnCategoryNext) {
            isEnabled = isActive
            // 활성화 상태에 따른 텍스트 색상 변경
            setTextColor(if (isActive) Color.WHITE else Color.parseColor("#6581FF"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}