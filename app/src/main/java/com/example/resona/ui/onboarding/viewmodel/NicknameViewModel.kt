package com.example.resona.ui.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val memberRepository: MainRepository
) : ViewModel() {

    private val _nicknameSaveResult = MutableStateFlow<ApiResult<String>?>(null)
    val nicknameSaveResult: StateFlow<ApiResult<String>?> = _nicknameSaveResult

    fun saveNickname(nickname: String) {
        viewModelScope.launch {
            val result = memberRepository.saveNickname(nickname)
            _nicknameSaveResult.value = ApiResult.Success(nickname)
        }
    }
}