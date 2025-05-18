package com.example.edusync.presentation.views.changePasswordAfterForgot

data class ChangePasswordAfterForgotState(
    val newPassword: String = "",
    val passwordError: String = "",
    val generalError: String = ""
)