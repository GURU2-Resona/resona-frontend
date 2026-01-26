package com.example.resona.ui.my

import com.example.resona.data.dto.MemberProfileResponseDto

data class MyUiState(
    val isLoading: Boolean = false,
    val profile: MemberProfileResponseDto? = null,
    val error: String? = null
)