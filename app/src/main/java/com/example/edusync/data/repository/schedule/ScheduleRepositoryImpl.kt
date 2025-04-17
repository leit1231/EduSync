package com.example.edusync.data.repository.schedule

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.ScheduleResponse
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import retrofit2.Response

class ScheduleRepositoryImpl(
    private val api: EduSyncApiService,
    private val encryptedPrefs: EncryptedSharedPreference
) : ScheduleRepository {

    override suspend fun getGroupSchedule(groupId: Int): Result<ScheduleResponse> =
        executeWithToken {
            api.getScheduleByGroup(groupId)
        }

    private suspend fun <T> executeWithToken(apiCall: suspend (String) -> Response<T>): Result<T> {
        val accessToken = encryptedPrefs.getAccessToken() ?: return Result.failure(Exception("No access token"))

        val response = apiCall(accessToken)
        if (response.code() != 401) return handleApiResponse(response)

        val refreshToken = encryptedPrefs.getRefreshToken() ?: return Result.failure(Exception("No refresh token"))
        val refreshResult = safeApiCall { api.refresh(RefreshRequest(refreshToken)) }

        return if (refreshResult.isSuccess) {
            val newToken = refreshResult.getOrNull()?.access_token ?: return Result.failure(Exception("Refresh failed"))
            encryptedPrefs.saveAccessToken(newToken)
            handleApiResponse(apiCall(newToken))
        } else {
            Result.failure(Exception("Token refresh failed"))
        }
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

    private fun <T> handleApiResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
        } else {
            Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
        }
    }
}