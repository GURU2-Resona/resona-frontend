package com.example.resona.data.dto

import com.google.gson.annotations.SerializedName

data class PostResponseDto(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("songTitle") val songTitle: String,
    @SerializedName("albumImage") val albumImage: String,
    @SerializedName("writerProfileImage") val writerProfileImage: String?, // Nullable로 변경
    @SerializedName("writerNickname") val writerNickname: String
)