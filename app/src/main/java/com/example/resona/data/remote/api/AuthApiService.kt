package com.example.resona.data.remote.api

import com.example.resona.data.remote.model.BaseResponse
import com.example.resona.data.remote.model.KakaoLoginRequest
import com.example.resona.data.remote.model.KakaoLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Date

interface AuthApiService {
    @POST("members/login/kakao")
    suspend fun loginWithKakao(@Body request: KakaoLoginRequest): Response<BaseResponse<KakaoLoginResponse>>

    @POST("members/login/master")
    suspend fun loginWithMasterAccount(): Response<BaseResponse<KakaoLoginResponse>>
}