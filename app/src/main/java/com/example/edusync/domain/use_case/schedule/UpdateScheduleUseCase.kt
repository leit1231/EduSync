package com.example.edusync.domain.use_case.schedule

import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.domain.repository.schedule.ScheduleRepository

class UpdateScheduleUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(scheduleId: Int, request: ScheduleUpdateRequest): Result<Unit> {
        return repository.updateSchedule(scheduleId, request)
    }
}