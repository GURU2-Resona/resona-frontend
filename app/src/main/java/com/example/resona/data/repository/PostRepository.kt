package com.example.resona.data.repository

import com.example.resona.data.dto.PostResponseDto
import com.example.resona.data.dto.PostCreateRequestDto
import com.example.resona.data.dto.PostCreateResponseDto
import com.example.resona.data.dto.PostDetailResponseDto
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.data.remote.api.PostApiService
import com.example.resona.data.remote.model.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val apiService: PostApiService
) {
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

    suspend fun createPost(
        request: PostCreateRequestDto
    ): ApiResult<PostCreateResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createPost(request)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.isSuccess) {
                        ApiResult.Success(baseResponse.result ?: throw Exception("No result data"))
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

    suspend fun getPostDetail(
        postId: Long
    ): ApiResult<PostDetailResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPostDetail(postId)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.isSuccess) {
                        ApiResult.Success(baseResponse.result ?: throw Exception("No result data"))
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

    suspend fun toggleScrap(
        postId: Long
    ): ApiResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.toggleScrap(postId)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.isSuccess) {
                        ApiResult.Success(baseResponse.result ?: "성공")
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

    suspend fun getScrappedPosts(): ApiResult<List<PostResponseDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPosts(null, null)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.isSuccess) ApiResult.Success(it.result ?: emptyList())
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