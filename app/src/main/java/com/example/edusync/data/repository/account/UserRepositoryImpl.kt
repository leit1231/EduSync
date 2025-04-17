package com.example.edusync.data.repository.account

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.data.remote.dto.LoginRequest
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.RegisterRequest
import com.example.edusync.domain.model.account.User
import com.example.edusync.domain.repository.account.UserRepository
import org.json.JSONObject
import retrofit2.Response

class UserRepositoryImpl(private val api: EduSyncApiService, private val encryptedSharedPreference: EncryptedSharedPreference) : UserRepository {

    override suspend fun register(request: RegisterRequest): Result<Unit> =
        handleApiResponse { api.register(request) }

    override suspend fun login(email: String, password: String): Result<AuthResponse> =
        safeApiCall { api.login(LoginRequest(email, password)) }
            .onSuccess { authResponse ->
                encryptedSharedPreference.saveAccessToken(authResponse.access_token)
                encryptedSharedPreference.saveRefreshToken(authResponse.refresh_token)
            }

    override suspend fun logout(): Result<Unit> = executeWithToken { token ->
        api.logout("Bearer $token")
    }.also { result ->
        if (result.isSuccess) {
            encryptedSharedPreference.clearTokens()
        }
    }

    override suspend fun refresh(refreshToken: String): Result<AuthResponse> =
        safeApiCall { api.refresh(RefreshRequest(refreshToken)) }
            .onSuccess { authResponse ->
                encryptedSharedPreference.saveAccessToken(authResponse.access_token)
                encryptedSharedPreference.saveRefreshToken(authResponse.refresh_token)
            }

    override suspend fun getProfile(): Result<User> = executeWithToken { token ->
        api.profile("Bearer $token")
    }.map { response ->
        User(
            id = response.id,
            email = response.email,
            fullName = response.full_name,
            isTeacher = response.is_teacher
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

    private suspend fun <T> executeWithToken(apiCall: suspend (String) -> Response<T>): Result<T> {
        val accessToken = encryptedSharedPreference.getAccessToken()
            ?: return Result.failure(Exception("No access token"))

        val response = apiCall(accessToken)
        if (response.code() != 401) {
            return handleApiResponse { response }
        }

        val refreshToken = encryptedSharedPreference.getRefreshToken()
            ?: return Result.failure(Exception("No refresh token"))

        val refreshResult = safeApiCall { api.refresh(RefreshRequest(refreshToken)) }
        if (refreshResult.isFailure) {
            encryptedSharedPreference.clearTokens()
            return Result.failure(Exception("Token refresh failed"))
        }

        val newToken = refreshResult.getOrNull()?.access_token
            ?: return Result.failure(Exception("Empty token after refresh"))

        encryptedSharedPreference.saveAccessToken(newToken)
        encryptedSharedPreference.saveRefreshToken(refreshResult.getOrNull()?.refresh_token ?: "")
        return handleApiResponse { apiCall(newToken) }
    }

}