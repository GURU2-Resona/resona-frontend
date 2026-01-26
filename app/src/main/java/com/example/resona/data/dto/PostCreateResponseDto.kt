package com.example.resona.data.dto

import com.google.gson.annotations.SerializedName

data class PostCreateResponseDto(
    @SerializedName("postId") val postId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)