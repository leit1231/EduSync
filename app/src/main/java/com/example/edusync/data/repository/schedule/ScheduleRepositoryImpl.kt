package com.example.edusync.data.repository.schedule

import android.util.Log
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.local.entities.AppDatabase
import com.example.edusync.data.local.entities.ScheduleDao
import com.example.edusync.data.local.entities.ScheduleEntity
import com.example.edusync.data.local.entities.TeacherInitialsDao
import com.example.edusync.data.local.entities.TeacherInitialsEntity
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.ScheduleItem
import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import kotlinx.serialization.json.Json

class ScheduleRepositoryImpl(
    private val api: EduSyncApiService,
    private val prefs: EncryptedSharedPreference,
    private val teacherDao: TeacherInitialsDao,
    private val scheduleDao: ScheduleDao,
    private val database: AppDatabase
) : ScheduleRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun getGroupSchedule(groupId: Int): Result<List<ScheduleItem>> =
        executor.execute { api.getScheduleByGroup(it, groupId) }

    override suspend fun getTeacherInitials(): Result<List<TeacherInitialsResponse>> {
        val local = teacherDao.getAll().map { it.toResponse() }
        if (local.isNotEmpty()) return Result.success(local)

        return executor.execute { api.getTeacherInitials(it) }.mapCatching { remote ->
            teacherDao.insertAll(remote.map { it.toEntity() })
            remote
        }
    }

    override suspend fun getScheduleByTeacher(initialsId: Int): Result<List<ScheduleItem>> =
        executor.execute { api.getScheduleByTeacher(it, initialsId) }

    override suspend fun updateSchedule(scheduleId: Int, request: ScheduleUpdateRequest): Result<Unit> =
        executor.execute { api.updateSchedule(it, scheduleId, request) }

    override suspend fun createSchedule(request: ScheduleUpdateRequest): Result<Unit> =
        executor.execute { api.createSchedule(it, request) }

    override suspend fun deleteSchedule(scheduleId: Int): Result<Unit> =
        executor.execute { api.deleteSchedule(it, scheduleId) }

    override suspend fun saveGroupSchedule(groupId: Int, schedule: List<ScheduleItem>) {
        val validated = schedule.map {
            it.copy(
                teacher = it.teacher ?: "",
                room = it.room ?: "",
                building = it.building ?: "",
                notice = it.notice ?: ""
            )
        }
        val json = Json.encodeToString(validated)
        database.scheduleDao().insert(
            ScheduleEntity(0, groupId, null, json, System.currentTimeMillis())
        )
    }

    override suspend fun saveTeacherSchedule(teacherId: Int, schedule: List<ScheduleItem>) {
        val json = Json.encodeToString(schedule)
        scheduleDao.insert(
            ScheduleEntity(0, null, teacherId, json, System.currentTimeMillis())
        )
    }

    override suspend fun getCachedGroupSchedule(groupId: Int): List<ScheduleItem>? {
        return try {
            scheduleDao.getGroupSchedule(groupId)?.let {
                Json.decodeFromString(it.scheduleJson)
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Ошибка парсинга кэшированного расписания группы: ${e.message}")
            null
        }
    }


    override suspend fun getCachedTeacherSchedule(teacherId: Int): List<ScheduleItem>? {
        return scheduleDao.getTeacherSchedule(teacherId)?.let { Json.decodeFromString(it.scheduleJson) }
    }

    suspend fun syncTeacherInitials() {
        val accessToken = prefs.getAccessToken() ?: return
        val response = api.getTeacherInitials(accessToken)
        if (response.isSuccessful) {
            teacherDao.deleteAll()
            teacherDao.insertAll(response.body()!!.map { it.toEntity() })
        }
    }

    private fun TeacherInitialsResponse.toEntity() = TeacherInitialsEntity(id, initials)
    private fun TeacherInitialsEntity.toResponse() = TeacherInitialsResponse(id, name)
}