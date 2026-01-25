package com.example.resona.data.repository

import com.example.resona.data.dto.PostResponseDto
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
                            // 서버에서 정의한 에러 메시지 처리
                            ApiResult.Error(Exception(baseResponse.message))
                        }
                    } ?: ApiResult.Error(Exception("Empty body"))
                } else {
                    // HTTP 통신 에러 (4xx, 5xx)
                    ApiResult.Error(Exception("HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                // 네트워크 연결 실패 등 예외 처리
                ApiResult.Error(e)
            }
        }
    }
}