package com.example.resona.data.repository

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
    private val postService: PostService
) {
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