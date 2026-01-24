package com.example.resona

import retrofit2.Call
import retrofit2.http.*

interface PostService {
    @POST("api/v1/posts")
    fun createPost(
        @Header("X-USER-ID") userId: Long,
        @Body request: PostCreateRequest
    ): Call<Unit>

    @GET("api/v1/posts/{postId}")
    fun getPostDetail(
        @Header("X-USER-ID") userId: Long,
        @Path("postId") postId: Long
    ): Call<ApiResponse<PostDetailResponse>>
}