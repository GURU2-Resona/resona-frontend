package com.example.resona.ui.login.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.event.Event
import com.example.resona.data.local.TokenManager
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.remote.model.KakaoLoginResponse
import com.example.resona.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager // TokenManager 주입
) : ViewModel() {

    private val _loginResult =
        MutableLiveData<Event<ApiResult<KakaoLoginResponse>>>()

    val loginResult: LiveData<Event<ApiResult<KakaoLoginResponse>>> =
        _loginResult

    fun loginWithKakao(accessToken: String) {
        viewModelScope.launch {
            _loginResult.value = Event(ApiResult.Loading)

            when (val result = repository.loginWithKakao(accessToken)) {
                is ApiResult.Success -> {
                    val response = result.data

                    // 토큰 저장
                    tokenManager.saveTokens(
                        response.accessToken,
                        response.refreshToken,
                        response.expireAt
                    )

                    _loginResult.value = Event(result)
                    Log.d("LoginViewModel", "서버 로그인 및 토큰 저장 성공: $response")
                }

                is ApiResult.Error -> {
                    Log.e("LoginError", "서버 로그인 실패", result.exception)
                    _loginResult.value = Event(result)
                }

                is ApiResult.Loading -> {
                    // 보통 Repository에서 Loading 안 내려서 여기 안 옴
                }
            }
        }
    }


    fun loginWithMasterAccount() {
        viewModelScope.launch {
            _loginResult.value = Event(ApiResult.Loading)

            when (val result = repository.loginWithMasterAccount()) {
                is ApiResult.Success -> {
                    val response = result.data

                    tokenManager.saveTokens(
                        response.accessToken,
                        response.refreshToken,
                        response.expireAt
                    )

                    _loginResult.value = Event(result)
                    Log.d("LoginViewModel", "마스터 로그인 성공: $response")
                }

                is ApiResult.Error -> {
                    Log.e("LoginError", "마스터 로그인 실패", result.exception)
                    _loginResult.value = Event(result)
                }

                is ApiResult.Loading -> {}
            }
        }
    }
}