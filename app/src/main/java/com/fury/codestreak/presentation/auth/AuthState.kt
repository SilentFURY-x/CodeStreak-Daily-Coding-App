package com.fury.codestreak.presentation.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoginMode: Boolean = true, // True = Login, False = Sign Up
    val isLoading: Boolean = false,
    val error: String? = null
)