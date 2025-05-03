package com.example.edusync.domain.use_case.chat

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.CreateChatRequest
import com.example.edusync.data.remote.dto.CreateChatResponse
import com.example.edusync.domain.repository.chat.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CreateChatUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(request: CreateChatRequest): Flow<Resource<CreateChatResponse>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.createChat(request)
            emit(result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error("Ошибка создания чата", null) }
            ))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}
