package com.example.edusync.domain.use_case.chat

import com.example.edusync.common.Resource
import com.example.edusync.domain.model.account.User
import com.example.edusync.domain.repository.chat.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetChatParticipantsUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(chatId: Int): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())
        val result = repository.getParticipants(chatId)
        emit(result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error("Ошибка загрузки участников", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
