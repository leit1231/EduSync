package com.example.edusync.data.repository.changePassword

import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.RequestResetPasswordDto
import com.example.edusync.data.remote.dto.ResetPasswordDto
import com.example.edusync.data.remote.dto.VerifyResetPasswordDto
import com.example.edusync.domain.repository.changePassword.ChangePassword
import org.json.JSONObject
import retrofit2.Response

class ChangePasswordImpl(
    private val api: EduSyncApiService
) : ChangePassword {

    override suspend fun requestPasswordReset(email: String): Result<String> =
        safeApiCall { api.requestPasswordReset(RequestResetPasswordDto(email = email)) }
            .map { it["message"] ?: "No message" }

    override suspend fun verifyResetCode(code: String): Result<String> =
        safeApiCall { api.verifyResetCode(VerifyResetPasswordDto(code = code)) }
            .map { it["message"] ?: "No message" }

    override suspend fun resetPassword(code: String, newPassword: String): Result<String> =
        safeApiCall { api.resetPassword(ResetPasswordDto(code = code, new_password = newPassword)) }
            .map { it["message"] ?: "No message" }

    private inline fun <T> safeApiCall(call: () -> Response<T>): Result<T> {
        return try {
            val response = call()

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Пустое тело ответа"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody ?: "{}").optString("error", "Ошибка: HTTP ${response.code()}")
                } catch (e: Exception) {
                    "Ошибка: HTTP ${response.code()}"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}