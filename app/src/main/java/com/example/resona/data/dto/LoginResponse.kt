package com.example.resona.data.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean
)