package com.example.resona

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PostService {
    @POST("api/v1/posts")
    fun createPost(
        @Header("X-USER-ID") userId: Long,
        @Body request: PostCreateRequest
    ): Call<Unit>
}