package com.example.edusync.data.repository

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.RefreshRequest
import org.json.JSONObject
import retrofit2.Response

class TokenRequestExecutor(
    private val prefs: EncryptedSharedPreference,
    private val api: EduSyncApiService
) {
    suspend fun <T> execute(call: suspend (String) -> Response<T>): Result<T> {
        val accessToken = prefs.getAccessToken() ?: return Result.failure(Exception("No access token"))

        var response = call(accessToken)
        if (isTokenInvalid(response)) {
            val refreshToken = prefs.getRefreshToken() ?: return Result.failure(Exception("No refresh token"))
            val refreshResponse = api.refresh(RefreshRequest(refreshToken))
            if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                prefs.clearUserData()
                return Result.failure(Exception("Token refresh failed"))
            }

            val newTokens = refreshResponse.body()!!
            prefs.saveAccessToken(newTokens.access_token)
            prefs.saveRefreshToken(newTokens.refresh_token)

            response = call(newTokens.access_token)
        }

        return handleResponse(response)
    }

    suspend fun <T> executeRawResponse(call: suspend (String) -> Response<T>): Result<Response<T>> {
        val accessToken = prefs.getAccessToken() ?: return Result.failure(Exception("No access token"))

        var response = call(accessToken)
        if (isTokenInvalid(response)) {
            val refreshToken = prefs.getRefreshToken() ?: return Result.failure(Exception("No refresh token"))
            val refreshResponse = api.refresh(RefreshRequest(refreshToken))
            if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                prefs.clearUserData()
                return Result.failure(Exception("Token refresh failed"))
            }

            val newTokens = refreshResponse.body()!!
            prefs.saveAccessToken(newTokens.access_token)
            prefs.saveRefreshToken(newTokens.refresh_token)

            response = call(newTokens.access_token)
        }

        return if (response.isSuccessful) {
            Result.success(response)
        } else {
            val errorText = response.errorBody()?.string()
            val message = try {
                JSONObject(errorText ?: "{}").optString("error", "HTTP ${response.code()}")
            } catch (e: Exception) {
                "HTTP ${response.code()}"
            }
            Result.failure(Exception(message))
        }
    }

    suspend fun executeNoContent(call: suspend (String) -> Response<Unit>): Result<Unit> {
        val accessToken = prefs.getAccessToken() ?: return Result.failure(Exception("No access token"))

        var response = call(accessToken)
        if (isTokenInvalid(response)) {
            val refreshToken = prefs.getRefreshToken() ?: return Result.failure(Exception("No refresh token"))
            val refreshResponse = api.refresh(RefreshRequest(refreshToken))
            if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                prefs.clearUserData()
                return Result.failure(Exception("Token refresh failed"))
            }

            val newTokens = refreshResponse.body()!!
            prefs.saveAccessToken(newTokens.access_token)
            prefs.saveRefreshToken(newTokens.refresh_token)

            response = call(newTokens.access_token)
        }

        return if (response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("HTTP ${response.code()} ${response.message()}"))
    }

    private fun <T> isTokenInvalid(response: Response<T>): Boolean {
        if (response.code() == 401) return true
        val errorBody = response.errorBody()?.string()
        return errorBody?.contains("Токен не существует", ignoreCase = true) == true
    }

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            val errorText = response.errorBody()?.string()
            val message = try {
                JSONObject(errorText ?: "{}").optString("error", "HTTP ${response.code()}")
            } catch (e: Exception) {
                "HTTP ${response.code()}"
            }
            Result.failure(Exception(message))
        }
    }
}