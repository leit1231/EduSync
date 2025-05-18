package com.example.edusync.presentation.views.login

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val generalError: String? = null
)