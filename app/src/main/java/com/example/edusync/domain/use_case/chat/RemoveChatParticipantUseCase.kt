package com.example.edusync.domain.use_case.chat

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.chat.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoveChatParticipantUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(chatId: Int, userId: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val result = repository.removeParticipant(chatId, userId)
        emit(result.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error("Ошибка удаления участника", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
