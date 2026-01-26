package com.example.resona.data.remote.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val memberRepository: MainRepository
) : ViewModel() {

    private val _nicknameSaveResult = MutableLiveData<ApiResult<Unit>>()
    val nicknameSaveResult: LiveData<ApiResult<Unit>> get() = _nicknameSaveResult

    fun saveNickname(nickname: String) {
        viewModelScope.launch {
            val result = memberRepository.saveNickname(nickname)
            _nicknameSaveResult.value = result
        }
    }
}
