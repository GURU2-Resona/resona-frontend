package com.example.resona.data.repository

import com.example.resona.data.dto.PostResponseDto
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.data.remote.api.PostApiService
import com.example.resona.data.remote.api.PostService
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.remote.model.PostCreateRequest
import com.example.resona.data.remote.model.PostCreateResponse
import com.example.resona.data.remote.model.PostDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val apiService: PostApiService,
    private val postService: PostService
) {
    /**
     * 게시글 목록 조회 (필터링 포함)
     */
    suspend fun fetchPosts(
        category: RecommendCategory?,
        scene: RecommendScene?
    ): ApiResult<List<PostResponseDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPosts(category, scene)
                if (response.isSuccessful) {
                    response.body()?.let { baseResponse ->
                        if (baseResponse.isSuccess) {
                            ApiResult.Success(baseResponse.result ?: emptyList())
                        } else {
                            ApiResult.Error(Exception(baseResponse.message))
                        }
                    } ?: ApiResult.Error(Exception("Empty body"))
                } else {
                    ApiResult.Error(Exception("HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    /**
     * 게시글 생성
     */
    suspend fun createPost(userId: Long, request: PostCreateRequest): ApiResult<PostCreateResponse> = withContext(Dispatchers.IO) {
        try {
            val response = postService.createPost(userId, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.isSuccess) ApiResult.Success(it.result!!)
                    else ApiResult.Error(Exception(it.message))
                } ?: ApiResult.Error(Exception("Empty Body"))
            } else {
                ApiResult.Error(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    /**
     * 게시글 상세 조회
     */
    suspend fun getPostDetail(userId: Long, postId: Long): ApiResult<PostDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = postService.getPostDetail(userId, postId)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.isSuccess) ApiResult.Success(it.result!!)
                    else ApiResult.Error(Exception(it.message))
                } ?: ApiResult.Error(Exception("Empty Body"))
            } else {
                ApiResult.Error(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}