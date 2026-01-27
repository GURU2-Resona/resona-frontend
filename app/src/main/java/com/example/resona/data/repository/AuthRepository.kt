package com.example.resona.data.repository

import com.example.resona.data.remote.api.AuthApiService
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.remote.model.KakaoLoginRequest
import com.example.resona.data.remote.model.KakaoLoginResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApiService
) {

    suspend fun loginWithKakao(
        accessToken: String
    ): ApiResult<KakaoLoginResponse> {
        return try {
            val response = api.loginWithKakao(
                KakaoLoginRequest(accessToken)
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.isSuccess && body.result != null) {
                    ApiResult.Success(body.result)
                } else {
                    ApiResult.Error(
                        Exception(body?.message ?: "로그인 실패")
                    )
                }
            } else {
                ApiResult.Error(
                    Exception("HTTP ${response.code()}")
                )
            }

        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun loginWithMasterAccount(): ApiResult<KakaoLoginResponse> {
        return try {
            val response = api.loginWithMasterAccount()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.isSuccess && body.result != null) {
                    ApiResult.Success(body.result)
                } else {
                    ApiResult.Error(
                        Exception(body?.message ?: "로그인 실패")
                    )
                }
            } else {
                ApiResult.Error(
                    Exception("HTTP ${response.code()}")
                )
            }

        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}