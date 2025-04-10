package com.example.edusync.domain.use_case.account

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.RegisterRequest
import com.example.edusync.domain.repository.account.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RegisterUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        fullName: String,
        isTeacher: Boolean,
        institutionId: Int,
        groupId: Int
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        val request = RegisterRequest(
            email = email,
            password = password,
            full_name = fullName,
            is_teacher = isTeacher,
            institution_id = institutionId,
            group_id = groupId
        )

        try {
            val result = repository.register(request)

            if (result.isSuccess) {
                emit(Resource.Success(Unit))
            } else {
                val exception = result.exceptionOrNull()
                val errorMessage = exception?.message ?: "Неизвестная ошибка"
                emit(Resource.Error(errorMessage, null))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}