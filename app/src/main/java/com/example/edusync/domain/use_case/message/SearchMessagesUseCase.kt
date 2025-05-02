package com.example.edusync.domain.use_case.message

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.domain.repository.message.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SearchMessagesUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(chatId: Int, query: String, limit: Int = 20, offset: Int = 0): Flow<Resource<List<MessageDto>>> = flow {
        emit(Resource.Loading())
        val result = repository.searchMessages(chatId, query, limit, offset)
        emit(result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error("Ошибка поиска сообщений", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
