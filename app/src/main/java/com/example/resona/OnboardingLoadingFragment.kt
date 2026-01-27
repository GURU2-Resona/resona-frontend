package com.example.resona

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.remote.model.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class OnboardingLoadingFragment :
    Fragment(R.layout.fragment_onboarding_loading) {

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onboardingViewModel.onboardingRecommendResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    findNavController().navigate(
                        R.id.navigation_onboarding_result
                    )
                }

                is ApiResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "추천에 실패했습니다. 다시 선택해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()

                    onboardingViewModel.resetRecommendState()
                    findNavController().navigate(
                        R.id.navigation_onboarding_recommend
                    )
                }

                is ApiResult.Loading -> {
                    // 아무것도 안 함
                    // fragment_onboarding_loading.xml이 로딩 UI
                }
                null -> {
                    // 초기 상태: 아무 것도 안 함
                }
            }
        }
    }
}
