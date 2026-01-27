package com.example.resona.data.remote.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.local.TokenManager // 1. TokenManager 임포트
import com.example.resona.data.remote.api.KakaoLoginResponse
import com.example.resona.data.repository.AuthRepository
import com.example.resona.data.event.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager // TokenManager 주입
) : ViewModel() {

    private val _loginResult = MutableLiveData<Event<KakaoLoginResponse>>()
    val loginResult: LiveData<Event<KakaoLoginResponse>> get() = _loginResult

    fun loginWithKakao(accessToken: String) {
        viewModelScope.launch {
            try {
                // 카카오 토큰으로 서버 로그인 요청
                val response = repository.loginWithKakao(accessToken)

                // 서버에서 받은 Access/Refresh 토큰을 DataStore에 저장
                tokenManager.saveTokens(response.accessToken, response.refreshToken, response.expireAt)

                _loginResult.value = Event(response)
                Log.d("LoginViewModel", "서버 로그인 및 토큰 저장 성공: $response")

            } catch (e: Exception) {
                Log.e("LoginError", "서버 로그인 실패 : ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    fun loginWithMasterAccount() {
        viewModelScope.launch {
            try {
                // 마스터 계정으로 서버 로그인 요청
                val response = repository.loginWithMasterAccount()

                // 서버에서 받은 Access 토큰을 DataStore에 저장
                tokenManager.saveTokens(response.accessToken, response.refreshToken, response.expireAt)

                _loginResult.value = Event(response)
                Log.d("LoginViewModel", "서버 로그인 및 토큰 저장 성공: $response")

            } catch (e: Exception) {
                Log.e("LoginError", "서버 로그인 실패 : ${e.message}", e)
                e.printStackTrace()
            }
        }
    }
}