package com.example.resona.di

import com.example.resona.BuildConfig
import com.example.resona.data.event.AuthEvent
import com.example.resona.data.event.AuthEventBus
import com.example.resona.data.local.TokenManager
import com.example.resona.data.remote.api.AuthApiService
import com.example.resona.data.remote.api.MyApiService
import com.example.resona.data.remote.api.PostApiService
import com.example.resona.data.remote.api.ResonaApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // NetworkModule.kt
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager, authEventBus: AuthEventBus): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val path = originalRequest.url.encodedPath

            // 인증이 필요 없는 경로는 헤더 없이 진행
            if (path.contains("members/login/kakao")) {
                return@Interceptor chain.proceed(originalRequest)
            }

            // 그 외의 요청에는 토큰 추가
            val accessToken = runBlocking {
                tokenManager.accessToken.first()
            }

            val request = originalRequest.newBuilder().apply {
                accessToken?.let {
                    addHeader("Authorization", "Bearer $it")
                }
            }.build()

            val response = chain.proceed(request)

            if (response.code == 401  && !authEventBus.isLoggedOut()) {
                runBlocking {
                    tokenManager.clearTokens()
                }
                authEventBus.emitLogoutOnce()
            }

            response
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor) // 인터셉터 등록
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ResonaApiService =
        retrofit.create(ResonaApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun providePostApiService(retrofit: Retrofit): PostApiService =
        retrofit.create(PostApiService::class.java)

    @Provides
    @Singleton
    fun provideMyApiService(retrofit: Retrofit): MyApiService =
        retrofit.create(MyApiService::class.java)
}