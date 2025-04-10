package com.example.edusync.domain.use_case.account

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.account.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LogoutUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(
        token: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.logout(token)

            if (result.isSuccess){
                emit(Resource.Success(Unit))
            }else{
                emit(Resource.Error("Ошибка выхода", null))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}