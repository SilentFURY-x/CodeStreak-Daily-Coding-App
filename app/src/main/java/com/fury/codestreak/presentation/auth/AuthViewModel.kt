package com.fury.codestreak.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    // The private state that only this ViewModel can change
    private val _state = mutableStateOf(AuthState())

    // The public state that the UI can read
    val state: State<AuthState> = _state

    fun onEvent(event: AuthEvent) {
        when(event) {
            is AuthEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            is AuthEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is AuthEvent.ToggleMode -> {
                _state.value = _state.value.copy(isLoginMode = !state.value.isLoginMode)
            }
            is AuthEvent.Submit -> {
                // We will add Firebase logic here later
                _state.value = _state.value.copy(isLoading = true)
            }
        }
    }
}

// Simple events to send from UI to ViewModel
sealed class AuthEvent {
    data class EmailChanged(val email: String): AuthEvent()
    data class PasswordChanged(val password: String): AuthEvent()
    object ToggleMode: AuthEvent()
    object Submit: AuthEvent()
}