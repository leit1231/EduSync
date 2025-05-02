package com.example.edusync.domain.use_case.poll

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.poll.PollRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeletePollUseCase(
    private val repository: PollRepository
) {
    operator fun invoke(chatId: Int, pollId: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val result = repository.deletePoll(chatId, pollId)
        emit(result.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error("Ошибка удаления опроса", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
