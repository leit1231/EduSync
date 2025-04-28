package com.example.edusync.data.repository.schedule

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.local.entities.AppDatabase
import com.example.edusync.data.local.entities.ScheduleDao
import com.example.edusync.data.local.entities.ScheduleEntity
import com.example.edusync.data.local.entities.TeacherInitialsDao
import com.example.edusync.data.local.entities.TeacherInitialsEntity
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.ScheduleItem
import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import kotlinx.serialization.json.Json
import retrofit2.Response

class ScheduleRepositoryImpl(
    private val api: EduSyncApiService,
    private val encryptedPrefs: EncryptedSharedPreference,
    private val teacherDao: TeacherInitialsDao,
    private val scheduleDao: ScheduleDao,
    private val database: AppDatabase
) : ScheduleRepository {

    override suspend fun getGroupSchedule(groupId: Int): Result<List<ScheduleItem>> =
        executeWithToken { token ->
            api.getScheduleByGroup(token, groupId)
        }

    override suspend fun getTeacherInitials(): Result<List<TeacherInitialsResponse>> {
        val localTeachers = teacherDao.getAll().map { it.toResponse() }
        if (localTeachers.isNotEmpty()) return Result.success(localTeachers)

        return executeWithToken { token ->
            api.getTeacherInitials(token)
        }.mapCatching { teachers ->
            teacherDao.insertAll(teachers.map { it.toEntity() })
            teachers
        }
    }

    override suspend fun getScheduleByTeacher(initialsId: Int): Result<List<ScheduleItem>> =
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

    override suspend fun createSchedule(request: ScheduleUpdateRequest): Result<Unit> =
        executeWithToken { token ->
            api.createSchedule(token, request)
        }

    override suspend fun deleteSchedule(scheduleId: Int): Result<Unit> =
        executeWithToken { token ->
            api.deleteSchedule(token, scheduleId)
        }

    override suspend fun saveGroupSchedule(groupId: Int, schedule: List<ScheduleItem>) {
        val validatedSchedule = schedule.map { item ->
            item.copy(
                teacher = item.teacher ?: "",
                room = item.room ?: "",
                building = item.building ?: "",
                notice = item.notice ?: ""
            )
        }
        val json = Json.encodeToString(validatedSchedule)
        database.scheduleDao().insert(
            ScheduleEntity(
                id = 0,
                groupId = groupId,
                teacherId = null,
                scheduleJson = json,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun saveTeacherSchedule(teacherId: Int, schedule: List<ScheduleItem>) {
        val json = Json.encodeToString(schedule)
        scheduleDao.insert(
            ScheduleEntity(
                id = 0,
                groupId = null,
                teacherId = teacherId,
                scheduleJson = json,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getCachedGroupSchedule(groupId: Int): List<ScheduleItem>? {
        val entity = scheduleDao.getGroupSchedule(groupId)
        return entity?.let { Json.decodeFromString(it.scheduleJson) }
    }

    override suspend fun getCachedTeacherSchedule(teacherId: Int): List<ScheduleItem>? {
        val entity = scheduleDao.getTeacherSchedule(teacherId)
        return entity?.let { Json.decodeFromString(it.scheduleJson) }
    }

    suspend fun syncSchedule(groupId: Int? = null, teacherId: Int? = null) {
        if (groupId != null) {
            val serverSchedule = getGroupSchedule(groupId).getOrNull()
            serverSchedule?.let {
                saveGroupSchedule(groupId, it)
            }
        }
        if (teacherId != null) {
            val serverSchedule = getScheduleByTeacher(teacherId).getOrNull()
            serverSchedule?.let {
                saveTeacherSchedule(teacherId, it)
            }
        }
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
        val response = api.getTeacherInitials(accessToken)
        if (response.isSuccessful) {
            teacherDao.deleteAll()
            teacherDao.insertAll(response.body()!!.map { it.toEntity() })
        }
    }

    private fun TeacherInitialsResponse.toEntity() = TeacherInitialsEntity(id, initials)
    private fun TeacherInitialsEntity.toResponse() = TeacherInitialsResponse(id, name)
}