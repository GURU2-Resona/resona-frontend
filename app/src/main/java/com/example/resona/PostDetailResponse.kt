package com.example.resona

data class ApiResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T
)

data class PostDetailResponse(
    val postId: Long,
    val writerProfileImage: String?,
    val writerNickname: String,
    val title: String,
    val content: String,
    val songTitle: String,
    val singer: String,
    val songUrl: String,
    val albumImage: String?,
    val isSaved: Boolean,
    val isMine: Boolean,
    val categoryName: String,
    val sceneName: String
)