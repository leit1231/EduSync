package com.example.edusync.domain.use_case.group

import com.example.edusync.common.Resource
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.repository.group.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetGroupsByInstitutionIdUseCase(
    private val repository: GroupRepository
) {
    operator fun invoke(
        institutionId: Int
    ): Flow<Resource<List<Group>>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.getGroupsByInstitution(institutionId)

            if (result.isSuccess) {
                val groups = result.getOrNull()
                emit(Resource.Success(groups))
            } else {
                emit(Resource.Error("Ошибка загрузки групп", null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}