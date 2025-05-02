package com.example.edusync.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val full_name: String,
    val is_teacher: Boolean,
    val institution_id: Int,
    val group_id: Int
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshRequest(
    val refresh_token: String
)

data class AuthResponse(
    val access_token: String,
    val refresh_token: String
)

data class UserProfileResponse(
    val user_id: Int,
    val email: String,
    val full_name: String,
    val is_teacher: Boolean,
    val institution_id: Int,
    val group_id: Int
)

data class UpdateProfileRequest(
    val full_name: String,
    val institution_id: Int,
    val group_id: Int
)