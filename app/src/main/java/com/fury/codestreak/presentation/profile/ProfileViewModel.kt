package com.fury.codestreak.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.codestreak.data.remote.CodeforcesApi
import com.fury.codestreak.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val email: String = "Guest",
    val codeforcesHandle: String = "",
    val codeforcesRating: Int? = null,
    val codeforcesRank: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDialogVisible: Boolean = false // To type the handle
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val api: CodeforcesApi
) : ViewModel() {

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    init {
        // 1. Get current Firebase User
        val user = authRepository.getCurrentUser()
        if (user != null) {
            _state.value = _state.value.copy(email = user.email ?: "User")
        }
    }

    fun onEvent(event: ProfileEvent) {
        when(event) {
            is ProfileEvent.ShowDialog -> {
                _state.value = _state.value.copy(isDialogVisible = true)
            }
            is ProfileEvent.HideDialog -> {
                _state.value = _state.value.copy(isDialogVisible = false)
            }
            is ProfileEvent.UpdateHandle -> {
                _state.value = _state.value.copy(codeforcesHandle = event.handle)
            }
            is ProfileEvent.FetchStats -> {
                fetchCodeforcesStats()
            }
            is ProfileEvent.Logout -> {
                // In a real app, call authRepository.logout()
                // For now, we just clear local state
            }
        }
    }

    private fun fetchCodeforcesStats() {
        val handle = state.value.codeforcesHandle
        if (handle.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isDialogVisible = false)
            try {
                val response = api.getUserInfo(handle)
                if (response.status == "OK" && !response.result.isNullOrEmpty()) {
                    val user = response.result[0]
                    _state.value = _state.value.copy(
                        isLoading = false,
                        codeforcesRating = user.rating,
                        codeforcesRank = user.rank,
                        error = null
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "User not found")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Network Error")
            }
        }
    }
}

sealed class ProfileEvent {
    object ShowDialog : ProfileEvent()
    object HideDialog : ProfileEvent()
    data class UpdateHandle(val handle: String) : ProfileEvent()
    object FetchStats : ProfileEvent()
    object Logout : ProfileEvent()
}