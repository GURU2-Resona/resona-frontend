package com.example.resona.data.remote.api

data class PostCreateRequest(
    val songTitle: String,
    val singer: String,
    val songUrl: String,
    val albumImage: String?,
    val title: String,
    val content: String,
    val category: TagRequest,
    val scene: TagRequest? = null
)

data class TagRequest(
    val id: Long? = null,
    val name: String? = null
)

data class PostCreateResponse(
    val postId: Long,
    val title: String
)