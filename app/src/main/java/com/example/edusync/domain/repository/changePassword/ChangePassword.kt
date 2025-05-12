package com.example.edusync.domain.repository.changePassword

interface ChangePassword {
    suspend fun requestPasswordReset(email: String): Result<String>
    suspend fun verifyResetCode(code: String): Result<String>
    suspend fun resetPassword(code: String, newPassword: String): Result<String>
}