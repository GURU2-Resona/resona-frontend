package com.example.resona

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.resona.data.remote.model.OnboardingViewModel
import com.example.resona.databinding.FragmentOnboardingRecommendBinding
import com.example.resona.ui.main.MainActivity
import kotlin.getValue

class OnboardingRecommendFragment : Fragment(R.layout.fragment_onboarding_recommend) {
    private var _binding: FragmentOnboardingRecommendBinding? = null
    private val binding get() = _binding!!
    private val onboardingViewModel : OnboardingViewModel by activityViewModels()
    private var selectedCategory: String = ""
    private var selectedScene: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingRecommendBinding.bind(view)
        (activity as? MainActivity)?.setTopBarTitle("음악 추천")

        val categoryButtons = listOf(
            binding.btnCategory1, binding.btnCategory2, binding.btnCategory3,
            binding.btnCategory4, binding.btnCategory5, binding.btnCategory6
        )

        val sceneButtons = listOf(
            binding.btnScene1, binding.btnScene2, binding.btnScene3,
            binding.btnScene4, binding.btnScene5, binding.btnScene6
        )

        binding.btnNext.isEnabled = false

        // 버튼 설정 + nextButton 체크
        setButtons(categoryButtons, binding.etCategory, sceneButtons, binding.etScene)

        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }

        binding.btnNext.setOnClickListener {
            onboardingViewModel.getOnboardingRecommend(selectedCategory,selectedScene)
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
                selectedCategory = button.text.toString()
                etCategory.text.clear()
                etCategory.clearFocus()
                checkNextButton(categoryButtons, etCategory, sceneButtons, etScene)
            }
        }

        // 씬 버튼 클릭
        sceneButtons.forEach { button ->
            button.setOnClickListener {
                sceneButtons.forEach { it.isSelected = it == button }
                selectedScene = button.text.toString()
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
        binding.btnNext.isEnabled = isCategorySelected && isSceneSelected
    }
}