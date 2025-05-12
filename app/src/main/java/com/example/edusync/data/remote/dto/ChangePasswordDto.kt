package com.example.edusync.data.remote.dto

data class RequestResetPasswordDto(
    val action: String = "reset_password",
    val email: String
)

data class VerifyResetPasswordDto(
    val action: String = "reset_password",
    val code: String
)

data class ResetPasswordDto(
    val code: String,
    val new_password: String
)