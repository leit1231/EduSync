package com.example.edusync.domain.use_case.teachers

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetTeacherInitialsUseCase(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<Resource<List<TeacherInitialsResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.getTeacherInitials()
            emit(result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error("Ошибка загрузки преподавателей", null) }
            ))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}