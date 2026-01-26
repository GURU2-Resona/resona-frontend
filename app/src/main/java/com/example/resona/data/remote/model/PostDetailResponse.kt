package com.example.resona.data.remote.model

import com.google.gson.annotations.SerializedName

data class PostDetailResponse(
    @SerializedName("postId") val postId: Long,
    @SerializedName("writerProfileImage") val writerProfileImage: String?,
    @SerializedName("writerNickname") val writerNickname: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("songTitle") val songTitle: String,
    @SerializedName("singer") val singer: String,
    @SerializedName("songUrl") val songUrl: String,
    @SerializedName("albumImage") val albumImage: String?,
    @SerializedName("isSaved") val isSaved: Boolean,
    @SerializedName("isMine") val isMine: Boolean,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("sceneName") val sceneName: String
)