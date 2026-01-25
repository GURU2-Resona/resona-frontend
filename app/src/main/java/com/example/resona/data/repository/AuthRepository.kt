package com.example.resona.data.repository

import com.example.resona.data.remote.api.AuthApiService
import com.example.resona.data.remote.api.KakaoLoginRequest
import com.example.resona.data.remote.api.KakaoLoginResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApiService
) {
    suspend fun loginWithKakao(accessToken: String): KakaoLoginResponse {
        val response = api.loginWithKakao(KakaoLoginRequest(accessToken))
        return response.result
    }
}