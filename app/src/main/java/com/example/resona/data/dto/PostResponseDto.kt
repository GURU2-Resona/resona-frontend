package com.example.resona.data.dto

import com.google.gson.annotations.SerializedName

data class PostResponseDto(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("songTitle") val songTitle: String,
    @SerializedName("songUrl") val songUrl: String,
    @SerializedName("writerProfileImage") val writerProfileImage: String?,
    @SerializedName("writerNickname") val writerNickname: String
)