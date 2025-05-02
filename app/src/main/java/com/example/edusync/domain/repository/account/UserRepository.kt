package com.example.edusync.domain.repository.account

import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.data.remote.dto.RegisterRequest
import com.example.edusync.data.remote.dto.UpdateProfileRequest
import com.example.edusync.domain.model.account.User

interface UserRepository {
    suspend fun register(request: RegisterRequest): Result<Unit>
    suspend fun login(email: String, password: String): Result<AuthResponse>
    suspend fun logout(): Result<Unit>
    suspend fun refresh(refreshToken: String): Result<AuthResponse>
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(request: UpdateProfileRequest): Result<AuthResponse>
}
