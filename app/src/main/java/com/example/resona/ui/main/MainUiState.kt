package com.example.resona.ui.main

data class MainUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)