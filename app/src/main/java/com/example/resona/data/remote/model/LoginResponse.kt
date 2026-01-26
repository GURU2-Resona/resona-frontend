package com.example.resona.data.remote.model

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean
)