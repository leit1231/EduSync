package com.example.edusync.presentation.views.forgotPassword

data class ForgotPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val generalError: String? = null
)