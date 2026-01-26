package com.example.resona.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    fun loadPosts(category: RecommendCategory? = null, scene: RecommendScene? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = repository.fetchPosts(category, scene)) {
                is ApiResult.Success -> {
                    _uiState.value = PostUiState(posts = result.data)
                }
                is ApiResult.Error -> {
                    _uiState.value = PostUiState(error = result.exception.message)
                }
                else -> Unit
            }
        }
    }
}