package com.example.resona.data.remote.model

import java.util.Date

data class KakaoLoginResponse(val accessToken: String,
                              val refreshToken: String,
                              val isNewUser: Boolean,
                              val expireAt: Date
)