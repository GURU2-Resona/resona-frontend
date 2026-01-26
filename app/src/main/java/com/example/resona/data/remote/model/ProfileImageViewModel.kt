package com.example.resona.data.remote.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resona.data.dto.ProfileImageResponse
import com.example.resona.data.repository.MainRepository
import com.example.resona.data.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileImageViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _profileImageResult =
        MutableLiveData<Event<ProfileImageResponse>>()
    val profileImageResult: LiveData<Event<ProfileImageResponse>>
        get() = _profileImageResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProfileImage() {
        viewModelScope.launch {
            when (val result = repository.getProfileImage()) {
                is ApiResult.Success -> {
                    _profileImageResult.value = Event(result.data)
                }
                is ApiResult.Error -> {
                    _error.value = result.exception.message
                }
            }
        }
    }
}