package com.example.edusync.data.repository.account

import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.data.remote.dto.LoginRequest
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.RegisterRequest
import com.example.edusync.domain.model.account.User
import com.example.edusync.domain.repository.account.UserRepository
import org.json.JSONObject
import retrofit2.Response

class UserRepositoryImpl(private val api: EduSyncApiService) : UserRepository {

    override suspend fun register(request: RegisterRequest): Result<Unit> =
        handleApiResponse { api.register(request) }

    override suspend fun login(email: String, password: String): Result<AuthResponse> =
        safeApiCall { api.login(LoginRequest(email, password)) }

    override suspend fun logout(token: String): Result<Unit> =
        safeApiCall { api.logout("Bearer $token") }

    override suspend fun refresh(refreshToken: String): Result<AuthResponse> =
        safeApiCall { api.refresh(RefreshRequest(refreshToken)) }

    override suspend fun getProfile(token: String): Result<User> =
        safeApiCall { api.profile("Bearer $token") }
            .map {
                User(
                    id = it.id,
                    email = it.email,
                    fullName = it.full_name,
                    isTeacher = it.is_teacher
                )
            }

    private inline fun <T> handleApiResponse(call: () -> Response<T>): Result<T> =
        try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody, response.code())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    private inline fun <T> safeApiCall(call: () -> Response<T>): Result<T> =
        try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Throwable("Empty body"))
            } else {
                Result.failure(Throwable("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun parseErrorMessage(errorBody: String?, statusCode: Int): String =
        try {
            errorBody?.let { JSONObject(it).optString("error", "Ошибка регистрации ($statusCode)") }
                ?: "Ошибка регистрации ($statusCode)"
        } catch (e: Exception) {
            "Ошибка регистрации ($statusCode)"
        }
}