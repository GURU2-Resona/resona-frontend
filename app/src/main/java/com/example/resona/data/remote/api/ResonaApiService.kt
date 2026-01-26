package com.example.resona.data.remote.api

import com.example.resona.data.dto.ProfileImageResponse
import com.example.resona.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.GET

interface ResonaApiService {
    @GET("api/v1/test")
    suspend fun getTestData(): Response<BaseResponse<Unit>>

    @GET("/members/profile/image")
    suspend fun getProfileImage():  Response<BaseResponse<ProfileImageResponse>>
}