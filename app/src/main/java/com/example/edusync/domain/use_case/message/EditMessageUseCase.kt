package com.example.edusync.domain.use_case.message

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.EditMessageResponse
import com.example.edusync.domain.repository.message.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class EditMessageUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(chatId: Int, messageId: Int, newText: String): Flow<Resource<EditMessageResponse>> = flow {
        emit(Resource.Loading())
        val result = repository.editMessage(chatId, messageId, newText)
        emit(result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error("Ошибка редактирования сообщения", null) }
        ))
    }.flowOn(Dispatchers.IO)
}