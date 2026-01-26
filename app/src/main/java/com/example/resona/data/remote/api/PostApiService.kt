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

    /**
     * 게시글 목록 조회
     * @param category 추천 카테고리 필터 (선택)
     * @param scene 추천 상황 필터 (선택)
     */
    @GET("posts")
    suspend fun getPosts(
        @Query("category") category: RecommendCategory?,
        @Query("scene") scene: RecommendScene?
    ): Response<BaseResponse<List<PostResponseDto>>>

    /**
     * 게시글 생성
     * @param request 게시글 작성 정보
     */
    @POST("posts")
    suspend fun createPost(
        @Body request: PostCreateRequestDto
    ): Response<BaseResponse<PostCreateResponseDto>>

    /**
     * 게시글 상세 조회
     * @param postId 조회할 게시글 ID
     */
    @GET("posts/{postId}")
    suspend fun getPostDetail(
        @Path("postId") postId: Long
    ): Response<BaseResponse<PostDetailResponseDto>>

    /**
     * 스크랩 토글 (저장/취소)
     * @param postId 스크랩할 게시글 ID
     * 리턴값: "스크랩 성공" 또는 "스크랩 취소" 메시지
     */
    @POST("posts/{postId}/scrap")
    suspend fun toggleScrap(
        @Path("postId") postId: Long
    ): Response<BaseResponse<String>>
}