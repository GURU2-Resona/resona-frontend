package com.example.resona.data.remote.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.remote.api.KakaoLoginResponse
import com.example.resona.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<KakaoLoginResponse>()
    val loginResult: LiveData<KakaoLoginResponse> get() = _loginResult

    fun loginWithKakao(accessToken: String) {
        viewModelScope.launch {
            try {
                val response = repository.loginWithKakao(accessToken)
                _loginResult.value = response
                Log.d("LoginViewModel", "서버 로그인 성공: $response")
            } catch (e: Exception) {
                Log.e("LoginError", "서버 로그인 실패 : ${e.message}", e)
                e.printStackTrace()
            }
        }
    }
}