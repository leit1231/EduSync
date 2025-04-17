package com.example.edusync.domain.repository.schedule

import com.example.edusync.data.remote.dto.ScheduleResponse

interface ScheduleRepository {
    suspend fun getGroupSchedule(groupId: Int): Result<ScheduleResponse>
}