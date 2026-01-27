package com.example.resona.data.remote.api

import com.example.resona.data.remote.model.BaseResponse
import com.example.resona.data.remote.model.CategoryRequest
import com.example.resona.data.remote.model.NicknameRequest
import com.example.resona.data.remote.model.OnboardingResponse
import com.example.resona.data.remote.model.ProfileImageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface OnboardingApiService {
    @GET("/members/profile/image")
    suspend fun getProfileImage():  Response<BaseResponse<ProfileImageResponse>>

    @PATCH("/members/nickname")
    suspend fun saveNickname(@Body request: NicknameRequest):  Response<BaseResponse<Unit>>

    @POST("/onboarding/recommend")
    suspend fun getOnboardingRecommend(@Body request: CategoryRequest): Response<BaseResponse<OnboardingResponse>>
}