package com.example.resona.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST

data class KakaoLoginRequest(val token: String)
data class KakaoLoginResponse(val accessToken: String,
                              val refreshToken: String,
                              val isNewUser: Boolean)

interface AuthApiService {
    @POST("members/login/kakao")
    suspend fun loginWithKakao(@Body request: KakaoLoginRequest): KakaoLoginResponse
}