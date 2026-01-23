package com.example.resona.data.repository

import com.example.resona.data.dto.LoginResponse

class AuthRepository {

    suspend fun kakaoLogin(
        accessToken: String
    ): LoginResponse {

        return LoginResponse(
            accessToken = "dummy",
            refreshToken = "dummy",
            isNewUser = true
        )
    }
}