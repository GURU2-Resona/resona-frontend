package com.example.resona

import android.graphics.Color
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

        val subject = arguments?.getString("userSubject")
        val content = arguments?.getString("userContent")

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

        // 초기 상태 설정
        nextButton.isEnabled = false
        nextButton.setTextColor(Color.parseColor("#6581FF"))

        val allButtons = categoryButtons + sceneButtons

        allButtons.forEach { button ->
            button.setOnClickListener {
                if (button.isSelected) {
                    button.isSelected = false
                } else {
                    val currentSelectedCount = allButtons.count { it.isSelected }
                    if (currentSelectedCount < 2) {
                        button.isSelected = true
                        etCategoryDirect.text.clear()
                        etSceneDirect.text.clear()
                    }
                }
                checkNextButton(allButtons, etCategoryDirect, etSceneDirect)
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    allButtons.forEach { it.isSelected = false }
                }
                checkNextButton(allButtons, etCategoryDirect, etSceneDirect)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etCategoryDirect.addTextChangedListener(textWatcher)
        etSceneDirect.addTextChangedListener(textWatcher)

        nextButton.setOnClickListener {
            val selectedTags = allButtons.filter { it.isSelected }.map { "#${it.text}" }.toMutableList()
            if (etCategoryDirect.text.isNotEmpty()) selectedTags.add("#${etCategoryDirect.text}")
            if (etSceneDirect.text.isNotEmpty()) selectedTags.add("#${etSceneDirect.text}")

            val finalBundle = Bundle().apply {
                putString("finalSubject", subject)
                putString("finalContent", content)
                putString("finalTag", selectedTags.joinToString(" "))
            }
            findNavController().navigate(R.id.action_postCategory_to_postDetail, finalBundle)
        }
    }

    private fun checkNextButton(allButtons: List<Button>, et1: EditText, et2: EditText) {
        val selectedCount = allButtons.count { it.isSelected }
        val directInputCount = (if (et1.text.isNotEmpty()) 1 else 0) + (if (et2.text.isNotEmpty()) 1 else 0)

        val isActive = (selectedCount + directInputCount == 2)
        nextButton.isEnabled = isActive

        if (isActive) {
            nextButton.setTextColor(Color.WHITE)
        } else {
            nextButton.setTextColor(Color.parseColor("#6581FF"))
        }
    }
}