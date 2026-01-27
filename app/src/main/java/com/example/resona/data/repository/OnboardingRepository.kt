package com.example.resona.data.repository

import android.util.Log
import com.example.resona.data.remote.api.OnboardingApiService
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.remote.model.CategoryRequest
import com.example.resona.data.remote.model.NicknameRequest
import com.example.resona.data.remote.model.OnboardingResponse
import com.example.resona.data.remote.model.ProfileImageResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepository @Inject constructor(
    private val apiService: OnboardingApiService
){
    suspend fun getProfileImage(): ApiResult<ProfileImageResponse> {
        return try {
            val response = apiService.getProfileImage()

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("ProfileImage", "Response: $response")
                if (body != null && body.isSuccess && body.result != null) {
                    ApiResult.Success(body.result)
                } else {
                    ApiResult.Error(
                        Exception(body?.message ?: "Empty response body")
                    )
                }
            } else {
                ApiResult.Error(
                    Exception("HTTP ${response.code()}: ${response.message()}")
                )
            }

        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun saveNickname(nickname: String): ApiResult<Unit> {
        return try {
            val response = apiService.saveNickname(
                NicknameRequest(nickname)
            )

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getOnboardingRecommend(request: CategoryRequest): ApiResult<OnboardingResponse> {
        return try {
            val response = apiService.getOnboardingRecommend(request)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.isSuccess && body.result != null) {
                    ApiResult.Success(body.result)
                } else {
                    ApiResult.Error(
                        Exception(body?.message ?: "Empty response body")
                    )
                }
            } else {
                ApiResult.Error(
                    Exception("HTTP ${response.code()}: ${response.message()}")
                )
            }

        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}