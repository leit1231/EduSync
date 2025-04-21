package com.example.edusync.domain.repository.schedule

import com.example.edusync.data.remote.dto.ScheduleResponse
import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.data.remote.dto.TeacherInitialsResponse

interface ScheduleRepository {
    suspend fun getGroupSchedule(groupId: Int): Result<ScheduleResponse>
    suspend fun getTeacherInitials(): Result<List<TeacherInitialsResponse>>
    suspend fun getScheduleByTeacher(initialsId: Int): Result<ScheduleResponse>
    suspend fun updateSchedule(scheduleId: Int, request: ScheduleUpdateRequest): Result<Unit>
    suspend fun deleteSchedule(scheduleId: Int): Result<Unit>
}