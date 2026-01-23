package com.example.resona.data.remote.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun loginWithKakao(accessToken: String) {
        viewModelScope.launch {
            val response = authRepository.kakaoLogin(accessToken)

            _loginResult.value = LoginResult(
                isNewUser = response.isNewUser
            )
        }
    }
}
