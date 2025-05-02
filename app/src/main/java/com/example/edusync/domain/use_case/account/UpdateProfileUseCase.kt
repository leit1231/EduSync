package com.example.edusync.domain.use_case.account

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.UpdateProfileRequest
import com.example.edusync.domain.repository.account.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UpdateProfileUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(request: UpdateProfileRequest): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.updateProfile(request)
            emit(result.fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error("Ошибка изменения профиля", null) }
            ))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}
