package com.example.edusync.domain.repository.schedule

import com.example.edusync.data.remote.dto.ScheduleItem
import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.data.remote.dto.TeacherInitialsResponse

interface ScheduleRepository {
    suspend fun getGroupSchedule(groupId: Int): Result<List<ScheduleItem>>
    suspend fun getTeacherInitials(): Result<List<TeacherInitialsResponse>>
    suspend fun getScheduleByTeacher(initialsId: Int): Result<List<ScheduleItem>>
    suspend fun createSchedule(request: ScheduleUpdateRequest): Result<Unit>
    suspend fun updateSchedule(scheduleId: Int, request: ScheduleUpdateRequest): Result<Unit>
    suspend fun deleteSchedule(scheduleId: Int): Result<Unit>
    suspend fun saveGroupSchedule(groupId: Int, schedule: List<ScheduleItem>)
    suspend fun saveTeacherSchedule(teacherId: Int, schedule: List<ScheduleItem>)
    suspend fun getCachedGroupSchedule(groupId: Int): List<ScheduleItem>?
    suspend fun getCachedTeacherSchedule(teacherId: Int): List<ScheduleItem>?
}