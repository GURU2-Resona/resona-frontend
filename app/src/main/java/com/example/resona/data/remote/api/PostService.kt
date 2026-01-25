package com.example.resona.data.remote.api

import com.example.resona.data.remote.model.BaseResponse
import com.example.resona.data.remote.model.PostCreateRequest
import com.example.resona.data.remote.model.PostCreateResponse
import com.example.resona.data.remote.model.PostDetailResponse
import retrofit2.Response
import retrofit2.http.*

interface PostService {
    @POST("posts")
    suspend fun createPost(
        @Header("X-USER-ID") userId: Long,
        @Body request: PostCreateRequest
    ): Response<BaseResponse<PostCreateResponse>>

    @GET("posts/{postId}")
    suspend fun getPostDetail(
        @Header("X-USER-ID") userId: Long,
        @Path("postId") postId: Long
    ): Response<BaseResponse<PostDetailResponse>>

    @POST("posts/{postId}/scrap")
    suspend fun toggleScrap(
        @Header("X-USER-ID") userId: Long,
        @Path("postId") postId: Long
    ): Response<BaseResponse<String>>
}