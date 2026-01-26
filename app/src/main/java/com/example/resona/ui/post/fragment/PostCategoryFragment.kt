package com.example.resona.ui.post.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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
import com.example.resona.ui.post.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostCategoryFragment : Fragment(R.layout.fragment_post_category) {
    private lateinit var nextButton: Button
    private val viewModel: PostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달받은 데이터 추출 (singer는 추출하지 않음)
        val subject = arguments?.getString("userSubject") ?: ""
        val content = arguments?.getString("userContent") ?: ""
        val videoId = arguments?.getString("videoId") ?: ""
        val songTitle = arguments?.getString("songTitle") ?: ""

        // UI 컴포넌트 초기화
        val categoryButtons = listOf(
            view.findViewById<Button>(R.id.btn_category_1),
            view.findViewById<Button>(R.id.btn_category_2),
            view.findViewById<Button>(R.id.btn_category_3),
            view.findViewById<Button>(R.id.btn_category_4),
            view.findViewById<Button>(R.id.btn_category_5),
            view.findViewById<Button>(R.id.btn_category_6)
        )
        val etCategoryDirect = view.findViewById<EditText>(R.id.et_category_direct)

        val sceneButtons = listOf(
            view.findViewById<Button>(R.id.btn_scene_1),
            view.findViewById<Button>(R.id.btn_scene_2),
            view.findViewById<Button>(R.id.btn_scene_3),
            view.findViewById<Button>(R.id.btn_scene_4),
            view.findViewById<Button>(R.id.btn_scene_5),
            view.findViewById<Button>(R.id.btn_scene_6)
        )
        val etSceneDirect = view.findViewById<EditText>(R.id.et_scene_direct)
        nextButton = view.findViewById(R.id.btn_category_next)

        // 단일 선택 로직 설정
        setupSelectionLogic(categoryButtons, etCategoryDirect, sceneButtons, etSceneDirect)

        // 게시글 생성 API 호출
        nextButton.setOnClickListener {
            val category = if (etCategoryDirect.text.isNotEmpty()) RecommendCategory.OTHER
            else RecommendCategory.entries[categoryButtons.indexOf(categoryButtons.find { it.isSelected })]

            val scene = if (etSceneDirect.text.isNotEmpty()) RecommendScene.OTHER
            else RecommendScene.entries[sceneButtons.indexOf(sceneButtons.find { it.isSelected })]

            // 가수 이름(singer) 필드를 제외한 DTO 생성
            val requestBody = PostCreateRequestDto(
                songTitle = songTitle,
                songUrl = "https://www.youtube.com/watch?v=$videoId",
                title = subject,
                content = content,
                category = category,
                customCategory = if (category == RecommendCategory.OTHER) etCategoryDirect.text.toString() else null,
                scene = scene,
                customScene = if (scene == RecommendScene.OTHER) etSceneDirect.text.toString() else null
            )

            viewLifecycleOwner.lifecycleScope.launch {
                val result = viewModel.repository.createPost(requestBody)
                when (result) {
                    is ApiResult.Success -> {
                        Toast.makeText(context, "추천글 등록 성공", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.navigation_home)
                    }
                    is ApiResult.Error -> {
                        // 백엔드에서 singer 필드 제약을 풀기 전까지는 여기서 400 에러가 뜰 수 있습니다.
                        Log.e("API_ERROR", "등록 실패: ${result.exception.message}")
                        Toast.makeText(context, "등록 실패: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupSelectionLogic(catBtns: List<Button>, catEt: EditText, sceBtns: List<Button>, sceEt: EditText) {
        catBtns.forEach { btn ->
            btn.setOnClickListener {
                catBtns.forEach { it.isSelected = false }; catEt.text.clear(); btn.isSelected = true
                checkNextButtonState(catBtns, catEt, sceBtns, sceEt)
            }
        }
        sceBtns.forEach { btn ->
            btn.setOnClickListener {
                sceBtns.forEach { it.isSelected = false }; sceEt.text.clear(); btn.isSelected = true
                checkNextButtonState(catBtns, catEt, sceBtns, sceEt)
            }
        }
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkNextButtonState(catBtns, catEt, sceBtns, sceEt) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        catEt.addTextChangedListener(watcher); sceEt.addTextChangedListener(watcher)
    }

    private fun checkNextButtonState(catBtns: List<Button>, catEt: EditText, sceBtns: List<Button>, sceEt: EditText) {
        val isActive = (catBtns.any { it.isSelected } || catEt.text.isNotEmpty()) &&
                (sceBtns.any { it.isSelected } || sceEt.text.isNotEmpty())
        nextButton.isEnabled = isActive
        nextButton.setTextColor(if (isActive) Color.WHITE else Color.parseColor("#6581FF"))
    }
}