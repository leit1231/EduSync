package com.example.edusync.domain.use_case.message

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.MessageResponse
import com.example.edusync.domain.repository.message.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeleteMessageUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(chatId: Int, messageId: Int): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.Loading())
        val result = repository.deleteMessage(chatId, messageId)
        emit(result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error("Ошибка удаления сообщения", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
