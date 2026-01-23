package com.example.resona.data.repository

import com.example.resona.data.remote.api.AuthApiService
import com.example.resona.data.remote.api.KakaoLoginRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService
) {
    suspend fun loginWithKakao(accessToken: String) =
        apiService.loginWithKakao(KakaoLoginRequest(accessToken))
}