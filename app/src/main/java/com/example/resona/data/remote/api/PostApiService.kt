package com.example.resona.data.remote.api

import com.example.resona.data.dto.PostResponseDto
import com.example.resona.data.dto.PostCreateRequestDto
import com.example.resona.data.dto.PostCreateResponseDto
import com.example.resona.data.dto.PostDetailResponseDto
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApiService {

    // 게시글 목록 조회
    @GET("posts")
    suspend fun getPosts(
        @Query("category") category: RecommendCategory?,
        @Query("scene") scene: RecommendScene?
    ): Response<BaseResponse<List<PostResponseDto>>>

    // 게시글 생성
    @POST("posts")
    suspend fun createPost(
        @Body request: PostCreateRequestDto
    ): Response<BaseResponse<PostCreateResponseDto>>

    // 게시글 상세 조회
    @GET("posts/{postId}")
    suspend fun getPostDetail(
        @Path("postId") postId: Long
    ): Response<BaseResponse<PostDetailResponseDto>>

    // 스크랩 토글
    @POST("posts/{postId}/scrap")
    suspend fun toggleScrap(
        @Path("postId") postId: Long
    ): Response<BaseResponse<String>>

    // 회원별 추천글 조회
    @GET("posts/members/{writerId}")
    suspend fun getOtherMemberPosts(
        @Path("writerId") writerId: Long,
        @Query("category") category: RecommendCategory?,
        @Query("scene") scene: RecommendScene?
    ): Response<BaseResponse<List<PostResponseDto>>>
}