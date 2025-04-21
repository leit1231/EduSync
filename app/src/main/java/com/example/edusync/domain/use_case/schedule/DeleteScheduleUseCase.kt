package com.example.edusync.domain.use_case.schedule

import com.example.edusync.domain.repository.schedule.ScheduleRepository

class DeleteScheduleUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(scheduleId: Int): Result<Unit> {
        return repository.deleteSchedule(scheduleId)
    }
}