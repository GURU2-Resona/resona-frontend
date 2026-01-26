package com.example.resona.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.remote.model.ApiResult
import com.example.resona.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = MainUiState(isLoading = true)

            when (val result = repository.fetchTestData()) {
                is ApiResult.Success -> {
                    _uiState.value = MainUiState(
                        isLoading = false,
                        success = true
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = MainUiState(
                        isLoading = false,
                        error = result.exception.message ?: "Unknown error"
                    )
                }

                else -> {}
            }
        }
    }
}