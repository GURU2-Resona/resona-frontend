package com.example.resona.data.remote.model

import com.google.gson.annotations.SerializedName

data class PostCreateResponse(
    @SerializedName("postId") val postId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)