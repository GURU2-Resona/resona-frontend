package com.example.resona.ui.onboarding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.remote.model.CategoryRequest
import com.example.resona.data.remote.model.OnboardingResponse
import com.example.resona.data.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val memberRepository: OnboardingRepository
) : ViewModel() {

    private val _onboardingRecommendResult = MutableLiveData<ApiResult<OnboardingResponse>?>()
    val onboardingRecommendResult: LiveData<ApiResult<OnboardingResponse>?> get() = _onboardingRecommendResult

    fun getOnboardingRecommend(category: String, scene: String) {
        viewModelScope.launch {
            val result = memberRepository.getOnboardingRecommend(CategoryRequest(category, scene))
            _onboardingRecommendResult.value = result
        }
    }

    fun resetRecommendState() {
        _onboardingRecommendResult.value = null
    }
}