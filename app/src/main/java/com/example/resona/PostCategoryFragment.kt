package com.example.resona

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class PostCategoryFragment : Fragment(R.layout.fragment_post_category) {
    private lateinit var nextButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 카테고리 버튼들 연결 (6개)
        val categoryButtons = listOf(
            view.findViewById<Button>(R.id.btn_category_1),
            view.findViewById<Button>(R.id.btn_category_2),
            view.findViewById<Button>(R.id.btn_category_3),
            view.findViewById<Button>(R.id.btn_category_4),
            view.findViewById<Button>(R.id.btn_category_5),
            view.findViewById<Button>(R.id.btn_category_6)
        )
        val etCategoryDirect = view.findViewById<EditText>(R.id.et_category_direct)

        // 2. 상황 버튼들 연결 (요청하신 대로 6개로 수정)
        val sceneButtons = listOf(
            view.findViewById<Button>(R.id.btn_scene_1),
            view.findViewById<Button>(R.id.btn_scene_2),
            view.findViewById<Button>(R.id.btn_scene_3),
            view.findViewById<Button>(R.id.btn_scene_4),
            view.findViewById<Button>(R.id.btn_scene_5),
            view.findViewById<Button>(R.id.btn_scene_6)
        )
        val etSceneDirect = view.findViewById<EditText>(R.id.et_scene_direct)

        // 3. 다음 버튼 연결 (첫 화면과 동일 디자인)
        nextButton = view.findViewById(R.id.btn_category_next)

        // 팀원분의 로직 적용 (버튼 설정 및 체크)
        setButtons(categoryButtons, etCategoryDirect, sceneButtons, etSceneDirect)

        // 다음 버튼 클릭 시 동작
        nextButton.setOnClickListener {
            // TODO: 마지막 단계 완료 시 이동할 화면 (예: 홈 화면 등) 연결 필요
            // findNavController().navigate(R.id.action_navigation_post_category_to_home)
        }
    }

    private fun setButtons(
        categoryButtons: List<Button>,
        etCategory: EditText,
        sceneButtons: List<Button>,
        etScene: EditText
    ) {
        // 카테고리 버튼 클릭 리스너
        categoryButtons.forEach { button ->
            button.setOnClickListener {
                categoryButtons.forEach { it.isSelected = (it == button) }
                etCategory.text.clear() // 버튼 누르면 직접 입력창 초기화
                checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
            }
        }

        // 상황 버튼 클릭 리스너 (6개 모두 적용)
        sceneButtons.forEach { button ->
            button.setOnClickListener {
                sceneButtons.forEach { it.isSelected = (it == button) }
                etScene.text.clear() // 버튼 누르면 직접 입력창 초기화
                checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
            }
        }

        // 카테고리 직접 입력 감지
        etCategory.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) categoryButtons.forEach { it.isSelected = false }
                checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 상황 직접 입력 감지
        etScene.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) sceneButtons.forEach { it.isSelected = false }
                checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // 카테고리와 상황이 모두 준비되었는지 체크
    private fun checkNextButton(
        categoryButtons: List<Button>,
        etCategory: EditText,
        sceneButtons: List<Button>,
        etScene: EditText
    ) {
        val isCategoryReady = categoryButtons.any { it.isSelected } || etCategory.text.isNotEmpty()
        val isSceneReady = sceneButtons.any { it.isSelected } || etScene.text.isNotEmpty()

        // 둘 다 만족해야 다음 버튼 활성화
        nextButton.isEnabled = isCategoryReady && isSceneReady
    }
}