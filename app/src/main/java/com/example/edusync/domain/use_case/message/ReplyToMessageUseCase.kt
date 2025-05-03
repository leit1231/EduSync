package com.example.edusync.domain.use_case.message

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.MessageResponse
import com.example.edusync.domain.repository.message.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ReplyToMessageUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(chatId: Int, messageId: Int, text: String): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.Loading())
        val result = repository.replyToMessage(chatId, messageId, text)
        emit(result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error("Ошибка ответа на сообщение", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
