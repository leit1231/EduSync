package com.example.edusync.presentation.views.login

data class LoginState(
    val email: String? = null,
    val password: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)