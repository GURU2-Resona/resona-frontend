package com.example.resona.data.remote.api

import com.example.resona.data.dto.PostResponseDto
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApiService {

    @GET("posts")
    suspend fun getPosts(
        @Query("category") category: RecommendCategory?,
        @Query("scene") scene: RecommendScene?
    ): Response<BaseResponse<List<PostResponseDto>>>

}