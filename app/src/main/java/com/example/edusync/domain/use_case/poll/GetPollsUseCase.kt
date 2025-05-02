package com.example.edusync.domain.use_case.poll

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.PollDto
import com.example.edusync.domain.repository.poll.PollRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetPollsUseCase(
    private val repository: PollRepository
) {
    operator fun invoke(chatId: Int): Flow<Resource<List<PollDto>>> = flow {
        emit(Resource.Loading())
        val result = repository.getPolls(chatId)
        emit(result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error("Ошибка загрузки опросов", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
