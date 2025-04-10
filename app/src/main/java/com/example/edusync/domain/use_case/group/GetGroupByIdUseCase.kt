package com.example.edusync.domain.use_case.group

import com.example.edusync.common.Resource
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.repository.group.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetGroupByIdUseCase(
    private val repository: GroupRepository
) {
    operator fun invoke(
        groupId: Int
    ): Flow<Resource<Group>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.getGroupById(groupId)

            if (result.isSuccess){
                val group = result.getOrNull()
                emit(Resource.Success(group))
            }else{
                emit(Resource.Error("Ошибка получения группы", null))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}