package com.example.resona.data.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import kotlin.math.log

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getAccessToken()
        Log.d("token",token.toString());

        // 로그인 / 토큰 없는 요청은 그냥 통과
        if (token.isNullOrEmpty()) {
            Log.d("token","실패");
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}
