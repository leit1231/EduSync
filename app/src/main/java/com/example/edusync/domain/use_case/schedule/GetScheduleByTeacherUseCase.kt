package com.example.edusync.domain.use_case.schedule

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.ScheduleResponse
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetScheduleByTeacherUseCase(
    private val repository: ScheduleRepository
) {
    operator fun invoke(initialsId: Int): Flow<Resource<ScheduleResponse>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.getScheduleByTeacher(initialsId)
            emit(result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error("Ошибка загрузки расписания", null) }
            ))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}