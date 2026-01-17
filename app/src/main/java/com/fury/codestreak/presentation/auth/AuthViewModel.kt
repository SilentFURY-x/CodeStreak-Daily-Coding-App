package com.fury.codestreak.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.codestreak.domain.repository.AuthRepository
import com.fury.codestreak.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    // 1. Create a Channel for "One-time" UI Events (like Navigation)
    private val _uiEvent = Channel<AuthUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AuthEvent) {
        when(event) {
            is AuthEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            is AuthEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is AuthEvent.ToggleMode -> {
                _state.value = _state.value.copy(
                    isLoginMode = !state.value.isLoginMode,
                    error = null
                )
            }
            is AuthEvent.Submit -> {
                authenticate()
            }
            is AuthEvent.SignInWithGoogle -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    val result = repository.signInWithGoogle(event.idToken)
                    handleAuthResult(result)
                }
            }
            is AuthEvent.SignInWithGithub -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    val result = repository.signInWithGithub(event.activity)
                    handleAuthResult(result)
                }
            }
        }
    }

    private fun authenticate() {
        val email = state.value.email
        val password = state.value.password

        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = if (state.value.isLoginMode) {
                repository.login(email, password)
            } else {
                repository.signup(email, password)
            }
            handleAuthResult(result)
        }
    }

    private suspend fun handleAuthResult(result: Resource<com.google.firebase.auth.FirebaseUser>) {
        when (result) {
            is Resource.Success -> {
                _state.value = _state.value.copy(isLoading = false, error = null)
                // 2. Send the Success Signal!
                _uiEvent.send(AuthUiEvent.NavigateToHome)
            }
            is Resource.Error -> {
                _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
            is Resource.Loading -> {
                _state.value = _state.value.copy(isLoading = true)
            }
        }
    }
}

// Events sent from UI to ViewModel
sealed class AuthEvent {
    data class EmailChanged(val email: String): AuthEvent()
    data class PasswordChanged(val password: String): AuthEvent()
    object ToggleMode: AuthEvent()
    object Submit: AuthEvent()
    data class SignInWithGoogle(val idToken: String) : AuthEvent()
    data class SignInWithGithub(val activity: android.app.Activity) : AuthEvent()
}

// New: Events sent from ViewModel to UI
sealed class AuthUiEvent {
    object NavigateToHome : AuthUiEvent()
}