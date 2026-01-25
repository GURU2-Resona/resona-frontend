package com.example.resona

import retrofit2.Call
import retrofit2.http.*

data class PostCreateResponse(
    val postId: Long,
    val title: String,
    val content: String
)

interface PostService {
    @POST("posts")
    fun createPost(
        @Header("X-USER-ID") userId: Long,
        @Body request: PostCreateRequest
    ): Call<ApiResponse<PostCreateResponse>>

    @GET("posts/{postId}")
    fun getPostDetail(
        @Header("X-USER-ID") userId: Long,
        @Path("postId") postId: Long
    ): Call<ApiResponse<PostDetailResponse>>

    @POST("posts/{postId}/scrap")
    fun toggleScrap(
        @Header("X-USER-ID") userId: Long,
        @Path("postId") postId: Long
    ): Call<ApiResponse<String>>
}