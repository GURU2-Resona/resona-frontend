package com.example.resona

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resona.MainActivity
import com.example.resona.R

class OnboardingRecommendFragment : Fragment(R.layout.fragment_onboarding_recommend) {

    private lateinit var nextButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setTopBarTitle("음악 추천")

        val btnCategory1 = view.findViewById<Button>(R.id.btn_category_1)
        val btnCategory2 = view.findViewById<Button>(R.id.btn_category_2)
        val btnCategory3 = view.findViewById<Button>(R.id.btn_category_3)
        val btnCategory4 = view.findViewById<Button>(R.id.btn_category_4)
        val btnCategory5 = view.findViewById<Button>(R.id.btn_category_5)
        val btnCategory6 = view.findViewById<Button>(R.id.btn_category_6)
        val etCategory = view.findViewById<EditText>(R.id.et_category)

        val categoryButtons = listOf(
            btnCategory1, btnCategory2, btnCategory3,
            btnCategory4, btnCategory5, btnCategory6
        )

        val btnScene1 = view.findViewById<Button>(R.id.btn_scene_1)
        val btnScene2 = view.findViewById<Button>(R.id.btn_scene_2)
        val btnScene3 = view.findViewById<Button>(R.id.btn_scene_3)
        val btnScene4 = view.findViewById<Button>(R.id.btn_scene_4)
        val btnScene5 = view.findViewById<Button>(R.id.btn_scene_5)
        val btnScene6 = view.findViewById<Button>(R.id.btn_scene_6)
        val etScene = view.findViewById<EditText>(R.id.et_scene)

        val sceneButtons = listOf(
            btnScene1, btnScene2, btnScene3,
            btnScene4, btnScene5, btnScene6
        )

        nextButton = view.findViewById(R.id.btn_next)
        nextButton.isEnabled = false
        val skipButton = view.findViewById<Button>(R.id.btn_skip)

        // 버튼 설정 + nextButton 체크
        setButtons(categoryButtons, etCategory, sceneButtons, etScene)

        skipButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }

        nextButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_onboarding_result)
        }
    }

    private fun setButtons(
        categoryButtons: List<Button>,
        etCategory: EditText,
        sceneButtons: List<Button>,
        etScene: EditText
    ) {
        // 카테고리 버튼 클릭
        categoryButtons.forEach { button ->
            button.setOnClickListener {
                categoryButtons.forEach { it.isSelected = it == button }
                etCategory.text.clear()
                etCategory.clearFocus()
                checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
            }
        }

        // 씬 버튼 클릭
        sceneButtons.forEach { button ->
            button.setOnClickListener {
                sceneButtons.forEach { it.isSelected = it == button }
                etScene.text.clear()
                etScene.clearFocus()
                checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
            }
        }

        // 카테고리 EditText 포커스
        etCategory.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) categoryButtons.forEach { it.isSelected = false }
            checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
        }
        etCategory.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkNextButton(categoryButtons, etCategory, sceneButtons, etScene) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 씬 EditText 포커스
        etScene.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) sceneButtons.forEach { it.isSelected = false }
            checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
        }
        etScene.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkNextButton(categoryButtons, etCategory, sceneButtons, etScene) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // nextButton 활성화 여부 체크
    private fun checkNextButton(
        categoryButtons: List<Button>,
        etCategory: EditText,
        sceneButtons: List<Button>,
        etScene: EditText
    ) {
        val isCategorySelected = categoryButtons.any { it.isSelected } || etCategory.text.isNotEmpty()
        val isSceneSelected = sceneButtons.any { it.isSelected } || etScene.text.isNotEmpty()
        nextButton.isEnabled = isCategorySelected && isSceneSelected
    }
}