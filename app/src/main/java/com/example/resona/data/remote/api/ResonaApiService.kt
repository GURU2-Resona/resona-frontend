package com.example.resona.data.remote.api

import com.example.resona.data.remote.model.CategoryRequest
import com.example.resona.data.remote.model.NicknameRequest
import com.example.resona.data.remote.model.OnboardingResponse
import com.example.resona.data.remote.model.ProfileImageResponse
import com.example.resona.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ResonaApiService {
    @GET("api/v1/test")
    suspend fun getTestData(): Response<BaseResponse<Unit>>
}