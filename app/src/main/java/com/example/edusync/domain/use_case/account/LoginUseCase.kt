package com.example.edusync.domain.use_case.account

import android.util.Log
import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.domain.repository.account.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LoginUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(
        email: String,
        password: String
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.login(email, password)
            Log.d("Login_in_account", "$result")

            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                emit(Resource.Success(authResponse))
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                emit(Resource.Error(errorMessage, null))
                Log.d("Login_in_account", "Ошибка авторизации: $errorMessage")
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}