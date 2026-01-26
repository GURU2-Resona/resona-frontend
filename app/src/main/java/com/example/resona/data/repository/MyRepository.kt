// com.example.resona.data.repository.MyRepository.kt
package com.example.resona.data.repository

import com.example.resona.data.dto.MemberProfileResponseDto
import com.example.resona.data.remote.api.MyApiService
import com.example.resona.data.remote.model.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyRepository @Inject constructor(
    private val apiService: MyApiService
) {
    suspend fun getMemberProfile(memberId: Long): ApiResult<MemberProfileResponseDto> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMemberProfile(memberId)
                if (response.isSuccessful) {
                    response.body()?.let { baseResponse ->
                        if (baseResponse.isSuccess) {
                            ApiResult.Success(baseResponse.result ?: throw Exception("데이터가 없습니다."))
                        } else {
                            ApiResult.Error(Exception(baseResponse.message))
                        }
                    } ?: ApiResult.Error(Exception("Empty Body"))
                } else {
                    ApiResult.Error(Exception("HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
}