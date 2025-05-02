package com.example.edusync.domain.use_case.message

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.domain.repository.message.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetMessagesUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(chatId: Int): Flow<Resource<List<MessageDto>>> = flow {
        emit(Resource.Loading())
        val result = repository.getMessages(chatId)
        emit(result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error("Ошибка загрузки сообщений", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
