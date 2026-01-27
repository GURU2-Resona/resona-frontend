package com.example.resona.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Date

data class KakaoLoginRequest(val token: String)
data class KakaoLoginResponse(val accessToken: String,
                              val refreshToken: String,
                              val isNewUser: Boolean,
                              val expireAt: Date
)
data class ApiResponse<T>(
    val isSuccess: Boolean,
    val status: String,
    val code: String,
    val message: String,
    val result: T
)


interface AuthApiService {
    @POST("members/login/kakao")
    suspend fun loginWithKakao(@Body request: KakaoLoginRequest): ApiResponse<KakaoLoginResponse>
}