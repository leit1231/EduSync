package com.example.edusync.domain.use_case.chat

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.chat.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeleteChatUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(chatId: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.deleteChat(chatId)
            emit(result.fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error("Ошибка удаления чата", null) }
            ))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}
