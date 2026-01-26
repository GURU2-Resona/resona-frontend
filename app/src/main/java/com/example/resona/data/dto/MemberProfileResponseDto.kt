package com.example.resona.data.dto

import com.google.gson.annotations.SerializedName

data class MemberProfileResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String?
)