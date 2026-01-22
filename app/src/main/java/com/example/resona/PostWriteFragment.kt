package com.example.resona

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class PostWriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nextButton = view.findViewById<Button>(R.id.btn_write_next)
        val etSubject = view.findViewById<EditText>(R.id.et_write_subject)
        val etContent = view.findViewById<EditText>(R.id.et_write_content)

        // 초기 상태 설정
        nextButton.isEnabled = false
        nextButton.setTextColor(Color.parseColor("#6581FF"))

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val subject = etSubject.text.toString().trim()
                val content = etContent.text.toString().trim()
                val isActive = subject.isNotEmpty() && content.isNotEmpty()

                nextButton.isEnabled = isActive

                if (isActive) {
                    nextButton.setTextColor(Color.WHITE)
                } else {
                    nextButton.setTextColor(Color.parseColor("#6581FF"))
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etSubject.addTextChangedListener(textWatcher)
        etContent.addTextChangedListener(textWatcher)

        nextButton.setOnClickListener {
            val subject = etSubject.text.toString()
            val content = etContent.text.toString()

            val bundle = Bundle().apply {
                putString("userSubject", subject)
                putString("userContent", content)
            }

            findNavController().navigate(R.id.action_postWrite_to_postCategory, bundle)
        }
    }
}