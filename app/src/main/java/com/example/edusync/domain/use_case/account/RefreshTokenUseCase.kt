package com.example.edusync.domain.use_case.account

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.domain.repository.account.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RefreshTokenUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(
        refreshToken: String
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.refresh(refreshToken)

            if (result.isSuccess){
                val authResponse = result.getOrNull()
                emit(Resource.Success(authResponse))
            }else{
                emit(Resource.Error("Ошибка обновления токена", null))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}