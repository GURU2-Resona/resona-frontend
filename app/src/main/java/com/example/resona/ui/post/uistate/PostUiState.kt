package com.example.resona.ui.post.uistate

import com.example.resona.data.dto.PostDetailResponseDto
import com.example.resona.data.dto.PostResponseDto

data class PostUiState(
    val isLoading: Boolean = false,
    val posts: List<PostResponseDto> = emptyList(),
    val postDetail: PostDetailResponseDto? = null,
    val error: String? = null
)