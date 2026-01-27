package com.example.resona.data.repository

import com.example.resona.data.remote.api.ResonaApiService
import com.example.resona.data.remote.model.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val apiService: ResonaApiService
) {
    suspend fun fetchTestData(): ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTestData()

                if (response.isSuccessful) {
                    response.body()?.let { baseResponse ->
                        if (baseResponse.isSuccess) {
                            ApiResult.Success(Unit)
                        } else {
                            // isSuccess가 false인 경우 (서버에서 정의한 에러)
                            ApiResult.Error(Exception(baseResponse.message))
                        }
                    } ?: ApiResult.Error(Exception("Empty response body"))
                } else {
                    // HTTP 에러 (4xx, 5xx)
                    response.errorBody()?.string()?.let {
                        ApiResult.Error(Exception("HTTP ${response.code()}: $it"))
                    }
                        ?: ApiResult.Error(Exception("HTTP ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }
}