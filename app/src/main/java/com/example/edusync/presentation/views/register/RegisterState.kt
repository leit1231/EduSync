package com.example.edusync.presentation.views.register

data class RegisterState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordConfirmation: String = "",
    val passwordConfirmationError: String? = null,
    val role: String? = null
)