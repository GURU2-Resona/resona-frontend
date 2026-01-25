package com.example.resona.data.remote.api

import com.example.resona.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApiService {
    @POST("api/v1/posts")
    suspend fun createPost(
        @Header("X-USER-ID") memberId: Long,
        @Body request: PostCreateRequest
    ): Response<BaseResponse<PostCreateResponse>>

    @POST("api/v1/posts/{postId}/scrap")
    suspend fun toggleScrap(
        @Header("X-USER-ID") memberId: Long,
        @Path("postId") postId: Long
    ): Response<BaseResponse<String>>
}