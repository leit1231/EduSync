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
        val accessToken = prefs.getAccessToken() ?: return Result.failure(Exception("Нет access токена"))

        var response = call(accessToken)
        if (isTokenInvalid(response)) {
            val refreshToken = prefs.getRefreshToken() ?: return Result.failure(Exception("Нет refresh токена"))
            val refreshResponse = api.refresh(RefreshRequest(refreshToken))
            if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                prefs.clearUserData()
                return Result.failure(Exception("Ошибка обновления токена"))
            }

            val newTokens = refreshResponse.body()!!
            prefs.saveAccessToken(newTokens.access_token)
            prefs.saveRefreshToken(newTokens.refresh_token)

            response = call(newTokens.access_token)
        }

        return handleResponse(response)
    }

    suspend fun <T> executeRawResponse(call: suspend (String) -> Response<T>): Result<Response<T>> {
        val accessToken = prefs.getAccessToken() ?: return Result.failure(Exception("Нет access токена"))

        var response = call(accessToken)
        if (isTokenInvalid(response)) {
            val refreshToken = prefs.getRefreshToken() ?: return Result.failure(Exception("Нет refresh токена"))
            val refreshResponse = api.refresh(RefreshRequest(refreshToken))
            if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                prefs.clearUserData()
                return Result.failure(Exception("Ошибка обновления токена"))
            }

            val newTokens = refreshResponse.body()!!
            prefs.saveAccessToken(newTokens.access_token)
            prefs.saveRefreshToken(newTokens.refresh_token)

            response = call(newTokens.access_token)
        }

        return if (response.isSuccessful) {
            Result.success(response)
        } else {
            val message = extractErrorMessage(response)
            Result.failure(Exception(message))
        }
    }

    suspend fun executeNoContent(call: suspend (String) -> Response<Unit>): Result<Unit> {
        val accessToken = prefs.getAccessToken() ?: return Result.failure(Exception("Нет access токена"))

        var response = call(accessToken)
        if (isTokenInvalid(response)) {
            val refreshToken = prefs.getRefreshToken() ?: return Result.failure(Exception("Нет refresh токена"))
            val refreshResponse = api.refresh(RefreshRequest(refreshToken))
            if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                prefs.clearUserData()
                return Result.failure(Exception("Ошибка обновления токена"))
            }

            val newTokens = refreshResponse.body()!!
            prefs.saveAccessToken(newTokens.access_token)
            prefs.saveRefreshToken(newTokens.refresh_token)

            response = call(newTokens.access_token)
        }

        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val message = extractErrorMessage(response)
            Result.failure(Exception(message))
        }
    }

    private fun <T> isTokenInvalid(response: Response<T>): Boolean {
        return response.code() == 401
    }

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let { Result.success(it) }
                ?: Result.failure(Exception("Пустое тело ответа"))
        } else {
            val message = extractErrorMessage(response)
            Result.failure(Exception(message))
        }
    }

    private fun <T> extractErrorMessage(response: Response<T>): String {
        val errorBody = response.errorBody()?.string()
        return try {
            JSONObject(errorBody ?: "{}").optString("error", "Ошибка ${response.code()}")
        } catch (e: Exception) {
            "Ошибка ${response.code()}"
        }
    }
}