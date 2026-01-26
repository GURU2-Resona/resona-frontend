package com.example.resona.ui.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

    fun loadMemberProfile(memberId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.getMemberProfile(memberId)) {
                is ApiResult.Success -> {
                    _uiState.value = MyUiState(profile = result.data)
                }
                is ApiResult.Error -> {
                    _uiState.value = MyUiState(error = result.exception.message)
                }
                else -> Unit
            }
        }
    }
}