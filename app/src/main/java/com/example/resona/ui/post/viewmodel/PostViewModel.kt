package com.example.resona.ui.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.repository.PostRepository
import com.example.resona.ui.post.uistate.PostUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    val repository: PostRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    /**
     * 게시글 상세 조회
     */
    fun loadPostDetail(postId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.getPostDetail(postId)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, postDetail = result.data)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                }
                else -> Unit
            }
        }
    }

    /**
     * 스크랩 토글
     */
    fun toggleScrap(postId: Long) {
        viewModelScope.launch {
            val result = repository.toggleScrap(postId)
            if (result is ApiResult.Success) {
                // 현재 postDetail의 isSaved 상태를 반전시켜 UI에 즉시 반영
                _uiState.value.postDetail?.let { currentDetail ->
                    val updatedDetail = currentDetail.copy(isSaved = !currentDetail.isSaved)
                    _uiState.value = _uiState.value.copy(postDetail = updatedDetail)
                }
            }
        }
    }

    /**
     * 게시글 목록 조회
     */
    fun loadPosts(category: RecommendCategory? = null, scene: RecommendScene? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.fetchPosts(category, scene)) {
                is ApiResult.Success -> _uiState.value = PostUiState(posts = result.data)
                is ApiResult.Error -> _uiState.value = PostUiState(error = result.exception.message)
                else -> Unit
            }
        }
    }

    /**
     * 특정 회원(작성자)의 추천글 조회
     */
    fun loadOtherMemberPosts(
        writerId: Long,
        category: RecommendCategory? = null,
        scene: RecommendScene? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.fetchOtherMemberPosts(writerId, category, scene)) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, posts = result.data, error = null)
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, error = result.exception.message)
                }

                else -> Unit
            }
        }
    }

    /**
     * 저장한 게시글 목록 조회 (필터링 적용)
     */
    fun loadScrappedPosts(
        category: RecommendCategory? = null,
        scene: RecommendScene? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.fetchScrappedPosts(category, scene)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, posts = result.data, error = null)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                }
                else -> Unit
            }
        }
    }

    /**
     * 내 추천글 목록 조회
     */
    fun loadMyPosts(
        category: RecommendCategory? = null,
        scene: RecommendScene? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.fetchMyPosts(category, scene)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, posts = result.data, error = null)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                }
                else -> Unit
            }
        }
    }
}