package com.example.resona

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
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostCategoryFragment : Fragment(R.layout.fragment_post_category) {
    private lateinit var nextButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subject = arguments?.getString("userSubject") ?: ""
        val content = arguments?.getString("userContent") ?: ""
        val videoId = arguments?.getString("videoId") ?: ""
        val songTitle = arguments?.getString("songTitle") ?: "Unknown Title"
        val singer = arguments?.getString("singer") ?: "Unknown Artist"

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
            val categoryRequest = if (etCategoryDirect.text.isNotEmpty()) {
                PostCreateRequest.TagRequest(name = etCategoryDirect.text.toString())
            } else {
                PostCreateRequest.TagRequest(id = 1L)
            }

            val sceneRequest = if (etSceneDirect.text.isNotEmpty()) {
                PostCreateRequest.TagRequest(name = etSceneDirect.text.toString())
            } else {
                PostCreateRequest.TagRequest(id = 1L)
            }

            val requestBody = PostCreateRequest(
                songTitle = songTitle,
                singer = singer,
                songUrl = "https://www.youtube.com/watch?v=$videoId",
                albumImage = "https://img.youtube.com/vi/$videoId/0.jpg",
                title = subject,
                content = content,
                category = categoryRequest,
                scene = sceneRequest
            )

            val service = RetrofitClient.getService().create(PostService::class.java)
            service.createPost(userId = 1L, requestBody).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "추천글 등록 완료", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.navigation_home)
                    } else {
                        Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("API", t.message.toString())
                }
            })
        }
    }

    private fun checkNextButton(allButtons: List<Button>, et1: EditText, et2: EditText) {
        val selectedCount = allButtons.count { it.isSelected }
        val directInputCount = (if (et1.text.isNotEmpty()) 1 else 0) + (if (et2.text.isNotEmpty()) 1 else 0)
        val isActive = (selectedCount + directInputCount == 2)
        nextButton.isEnabled = isActive
        nextButton.setTextColor(if (isActive) Color.WHITE else Color.parseColor("#6581FF"))
    }
}