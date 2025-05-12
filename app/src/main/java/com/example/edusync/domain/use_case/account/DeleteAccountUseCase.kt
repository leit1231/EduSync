package com.example.edusync.domain.use_case.account

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.account.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeleteAccountUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(refreshToken: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        val result = repository.deleteAccount(refreshToken)
        if (result.isSuccess) {
            emit(Resource.Success(result.getOrNull() ?: "Аккаунт удалён"))
        } else {
            emit(Resource.Error(result.exceptionOrNull()?.message ?: "Ошибка удаления аккаунта"))
        }
    }.flowOn(Dispatchers.IO)
}