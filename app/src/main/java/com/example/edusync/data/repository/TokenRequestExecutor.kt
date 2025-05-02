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
            val rawRefreshToken = prefs.getRefreshToken() ?: return Result.failure(Exception("No refresh token"))

            val refreshResponse = api.refresh(RefreshRequest(rawRefreshToken))
            if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                prefs.clearUserData()
                return Result.failure(Exception("Token refresh failed"))
            }

            val auth = refreshResponse.body()!!
            prefs.saveAccessToken(auth.access_token)
            prefs.saveRefreshToken(auth.refresh_token)

            response = call(auth.access_token)
        }

        return handleResponse(response)
    }

    private fun <T> isTokenInvalid(response: Response<T>): Boolean {
        if (response.code() == 401) return true
        val errorBody = response.errorBody()?.string()
        return errorBody?.contains("Токен не существует", ignoreCase = true) == true
    }

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty response"))
        } else {
            val error = response.errorBody()?.string()
            val message = try {
                JSONObject(error ?: "{}").optString("error", "HTTP ${response.code()}")
            } catch (e: Exception) {
                "HTTP ${response.code()}"
            }
            Result.failure(Exception(message))
        }
    }
}