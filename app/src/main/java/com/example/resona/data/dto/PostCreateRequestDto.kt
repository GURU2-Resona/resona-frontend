package com.example.resona.data.dto

import com.google.gson.annotations.SerializedName

data class PostCreateRequestDto(
    @SerializedName("songTitle") val songTitle: String,
    @SerializedName("singer") val singer: String,
    @SerializedName("songUrl") val songUrl: String,
    @SerializedName("albumImage") val albumImage: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("category") val category: TagRequest,
    @SerializedName("scene") val scene: TagRequest
) {
    data class TagRequest(
        @SerializedName("id") val id: Long? = null,
        @SerializedName("name") val name: String? = null
    )
}