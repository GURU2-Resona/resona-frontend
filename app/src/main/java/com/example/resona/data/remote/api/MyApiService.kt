package com.example.resona.data.remote.api

import com.example.resona.data.dto.MemberProfileResponseDto
import com.example.resona.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MyApiService {
    @GET("members/{memberId}")
    suspend fun getMemberProfile(
        @Path("memberId") memberId: Long
    ): Response<BaseResponse<MemberProfileResponseDto>>
}