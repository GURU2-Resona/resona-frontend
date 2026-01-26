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

    /**
     * 게시글 상세 조회
     */
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

    /**
     * 스크랩 토글
     */
    suspend fun toggleScrap(
        postId: Long
    ): ApiResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.toggleScrap(postId)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.isSuccess) {
                        ApiResult.Success(baseResponse.result ?: "스크랩 완료")
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

    /**
     * 회원별 추천글 조회
     */
    suspend fun fetchOtherMemberPosts(
        writerId: Long,
        category: RecommendCategory?,
        scene: RecommendScene?
    ): ApiResult<List<PostResponseDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getOtherMemberPosts(writerId, category, scene)
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

    /**
     * 저장한 게시글 목록 조회 (필터링 포함)
     */
    suspend fun fetchScrappedPosts(
        category: RecommendCategory?,
        scene: RecommendScene?
    ): ApiResult<List<PostResponseDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getScrappedPosts(category, scene)
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