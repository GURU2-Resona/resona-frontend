package com.example.resona.data.remote.api

import com.example.resona.data.dto.CategoryRequest
import com.example.resona.data.dto.NicknameRequest
import com.example.resona.data.dto.OnboardingResponse
import com.example.resona.data.dto.ProfileImageResponse
import com.example.resona.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ResonaApiService {
    @GET("api/v1/test")
    suspend fun getTestData(): Response<BaseResponse<Unit>>

    @GET("/members/profile/image")
    suspend fun getProfileImage():  Response<BaseResponse<ProfileImageResponse>>

    @PATCH("/members/nickname")
    suspend fun saveNickname(@Body request: NicknameRequest):  Response<BaseResponse<Unit>>

    @POST("/onboarding/recommend")
    suspend fun getOnboardingRecommend(@Body request: CategoryRequest): Response<BaseResponse<OnboardingResponse>>
}