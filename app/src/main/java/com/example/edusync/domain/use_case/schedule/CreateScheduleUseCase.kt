package com.example.edusync.domain.use_case.schedule

import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.domain.repository.schedule.ScheduleRepository

class CreateScheduleUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(request: ScheduleUpdateRequest): Result<Unit> {
        return repository.createSchedule(request)
    }
}