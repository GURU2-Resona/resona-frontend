package com.example.resona.data.remote.model // 반드시 이 패키지여야 합니다!

data class PostCreateRequest(
    val songTitle: String,
    val singer: String,
    val songUrl: String,
    val albumImage: String,
    val title: String,
    val content: String,
    val category: TagRequest,
    val scene: TagRequest
) {
    data class TagRequest(
        val id: Long? = null,
        val name: String? = null
    )
}