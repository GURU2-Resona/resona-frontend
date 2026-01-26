package com.example.resona.data.remote.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.dto.CategoryRequest
import com.example.resona.data.dto.OnboardingResponse
import com.example.resona.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val memberRepository: MainRepository
) : ViewModel() {

    private val _onboardingRecommendResult = MutableLiveData<ApiResult<OnboardingResponse>>()
    val onboardingRecommendResult: LiveData<ApiResult<OnboardingResponse>> get() = _onboardingRecommendResult

    fun getOnboardingRecommend(category: String, scene: String) {
        viewModelScope.launch {
            val result = memberRepository.getOnboardingRecommend(CategoryRequest(category, scene))
            _onboardingRecommendResult.value = result
        }
    }
}