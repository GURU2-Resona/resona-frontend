package com.example.resona.ui.post

import com.example.resona.data.dto.PostResponseDto

data class PostUiState(
    val isLoading: Boolean = false,
    val posts: List<PostResponseDto> = emptyList(),
    val error: String? = null
)