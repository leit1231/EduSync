package com.example.edusync.data.repository.schedule

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.local.entities.TeacherInitialsDao
import com.example.edusync.data.local.entities.TeacherInitialsEntity
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.ScheduleResponse
import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import retrofit2.Response

class ScheduleRepositoryImpl(
    private val api: EduSyncApiService,
    private val encryptedPrefs: EncryptedSharedPreference,
    private val teacherDao: TeacherInitialsDao
) : ScheduleRepository {

    override suspend fun getGroupSchedule(groupId: Int): Result<ScheduleResponse> =
        executeWithToken {
            api.getScheduleByGroup(groupId)
        }

    override suspend fun getTeacherInitials(): Result<List<TeacherInitialsResponse>> {
        val localTeachers = teacherDao.getAll().map { it.toResponse() }
        if (localTeachers.isNotEmpty()) return Result.success(localTeachers)

        return executeWithToken { token ->
            api.getTeacherInitials("Bearer $token")
        }.mapCatching { teachers ->
            teacherDao.insertAll(teachers.map { it.toEntity() })
            teachers
        }
    }

    override suspend fun getScheduleByTeacher(initialsId: Int): Result<ScheduleResponse> =
        executeWithToken { token ->
            api.getScheduleByTeacher(token, initialsId)
        }

    override suspend fun updateSchedule(
        scheduleId: Int,
        request: ScheduleUpdateRequest
    ): Result<Unit> =
        executeWithToken { token ->
            api.updateSchedule(token, scheduleId, request)
        }

    override suspend fun deleteSchedule(scheduleId: Int): Result<Unit> =
        executeWithToken { token ->
            api.deleteSchedule(token, scheduleId)
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

    suspend fun syncTeacherInitials() {
        val accessToken = encryptedPrefs.getAccessToken() ?: return
        val response = api.getTeacherInitials("Bearer $accessToken")
        if (response.isSuccessful) {
            teacherDao.deleteAll()
            teacherDao.insertAll(response.body()!!.map { it.toEntity() })
        }
    }

    private fun TeacherInitialsResponse.toEntity() = TeacherInitialsEntity(id, initials)
    private fun TeacherInitialsEntity.toResponse() = TeacherInitialsResponse(id, name)
}