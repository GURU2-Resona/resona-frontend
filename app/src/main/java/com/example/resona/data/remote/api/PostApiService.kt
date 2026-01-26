package com.example.resona.data.remote.api

import com.example.resona.data.dto.*
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.*

interface PostApiService {

    @GET("posts")
    suspend fun getPosts(
        @Query("category") category: RecommendCategory?,
        @Query("scene") scene: RecommendScene?
    ): Response<BaseResponse<List<PostResponseDto>>>

    @POST("posts")
    suspend fun createPost(
        @Body request: PostCreateRequestDto
    ): Response<BaseResponse<PostCreateResponseDto>>

    @GET("posts/{postId}")
    suspend fun getPostDetail(
        @Path("postId") postId: Long
    ): Response<BaseResponse<PostDetailResponseDto>>

    @POST("posts/{postId}/scrap")
    suspend fun toggleScrap(
        @Path("postId") postId: Long,
        @Header("X-USER-ID") userId: Long = 1L
    ): Response<BaseResponse<String>>
}