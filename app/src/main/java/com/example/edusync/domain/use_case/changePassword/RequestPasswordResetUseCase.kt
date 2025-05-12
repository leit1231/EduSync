package com.example.edusync.domain.use_case.changePassword

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.changePassword.ChangePassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RequestPasswordResetUseCase(
    private val repository: ChangePassword
) {
    operator fun invoke(email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        val result = repository.requestPasswordReset(email)
        if (result.isSuccess) {
            emit(Resource.Success(result.getOrNull() ?: "OK"))
        } else {
            emit(Resource.Error(result.exceptionOrNull()?.message ?: "Ошибка отправки кода"))
        }
    }.flowOn(Dispatchers.IO)
}