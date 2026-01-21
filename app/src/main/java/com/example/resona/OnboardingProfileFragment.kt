package com.example.resona

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class OnboardingProfileFragment : Fragment(R.layout.fragment_onboarding_profile) {
    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setTopBarTitle("정보 입력")

        var imageButton : ImageButton = view.findViewById<ImageButton>(R.id.btn_image)
        var inputEditText : EditText = view.findViewById<EditText>(R.id.et_nickname)
        var nextButton : Button = view.findViewById<Button>(R.id.btn_next)

        nextButton.isEnabled = false

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                val nickname = s.toString().trim()
                nextButton.isEnabled = nickname.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        nextButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_onboarding_recommend)
        }

    }
}