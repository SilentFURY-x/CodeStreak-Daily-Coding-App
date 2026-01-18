package com.fury.codestreak.presentation.profile

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.codestreak.data.remote.CodeforcesApi
import com.fury.codestreak.domain.model.User
import com.fury.codestreak.domain.repository.AuthRepository
import com.fury.codestreak.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: User? = null,
    // Codeforces Specifics
    val cfRating: Int? = null,
    val cfRank: String? = null,
    val cfAvatar: String? = null,
    // UI States
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDialogVisible: Boolean = false,
    val tempHandleInput: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository, // Inject UserRepo
    private val api: CodeforcesApi
) : ViewModel() {

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                // Listen to Real-Time User Data
                userRepository.getUserFlow(currentUser.uid).collectLatest { user ->
                    if (user != null) {
                        _state.value = _state.value.copy(user = user)
                        // If we have a saved handle, fetch stats automatically
                        if (!user.codeforcesHandle.isNullOrBlank() && _state.value.cfRating == null) {
                            fetchCodeforcesStats(user.codeforcesHandle)
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when(event) {
            is ProfileEvent.ShowDialog -> _state.value = _state.value.copy(isDialogVisible = true)
            is ProfileEvent.HideDialog -> _state.value = _state.value.copy(isDialogVisible = false)
            is ProfileEvent.UpdateHandleInput -> _state.value = _state.value.copy(tempHandleInput = event.handle)
            is ProfileEvent.ConnectCodeforces -> connectAndSaveHandle()
            is ProfileEvent.Logout -> { /* Handle Logout */ }
        }
    }

    private fun connectAndSaveHandle() {
        val handle = state.value.tempHandleInput
        if (handle.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isDialogVisible = false, error = null)
            try {
                // 1. Verify Handle with API
                val response = api.getUserInfo(handle)

                if (response.status == "OK" && !response.result.isNullOrEmpty()) {
                    val cfUser = response.result[0]

                    // 2. Save Handle to Firestore (Persistence)
                    val currentUser = _state.value.user
                    if (currentUser != null) {
                        val updatedUser = currentUser.copy(codeforcesHandle = cfUser.handle)
                        userRepository.createOrUpdateUser(updatedUser)
                    }

                    // 3. Update UI
                    _state.value = _state.value.copy(
                        isLoading = false,
                        cfRating = cfUser.rating,
                        cfRank = cfUser.rank,
                        cfAvatar = cfUser.avatar
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "Handle not found")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Network Error: ${e.message}")
            }
        }
    }

    private fun fetchCodeforcesStats(handle: String) {
        viewModelScope.launch {
            try {
                val response = api.getUserInfo(handle)
                if (response.status == "OK" && !response.result.isNullOrEmpty()) {
                    val cfUser = response.result[0]
                    _state.value = _state.value.copy(
                        cfRating = cfUser.rating,
                        cfRank = cfUser.rank,
                        cfAvatar = cfUser.avatar
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Silent fetch failed", e)
            }
        }
    }
}

sealed class ProfileEvent {
    object ShowDialog : ProfileEvent()
    object HideDialog : ProfileEvent()
    data class UpdateHandleInput(val handle: String) : ProfileEvent()
    object ConnectCodeforces : ProfileEvent()
    object Logout : ProfileEvent()
}