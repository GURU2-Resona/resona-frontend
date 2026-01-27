package com.example.resona.data.remote.model

import java.util.Date

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean,
    val expireAt: Date
)