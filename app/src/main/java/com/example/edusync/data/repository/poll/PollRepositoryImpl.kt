package com.example.edusync.data.repository.poll

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.CreatePollRequest
import com.example.edusync.data.remote.dto.VoteRequest
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.repository.poll.PollRepository

class PollRepositoryImpl(
    private val api: EduSyncApiService,
    prefs: EncryptedSharedPreference
) : PollRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun getPolls(chatId: Int) = executor.execute { api.getPolls(it, chatId) }
    override suspend fun createPoll(chatId: Int, request: CreatePollRequest) = executor.execute { api.createPoll(it, chatId, request) }
    override suspend fun vote(chatId: Int, pollId: Int, optionId: Int) =
        executor.execute { api.votePollOption(it, chatId, pollId, VoteRequest(optionId)) }

    override suspend fun unvote(chatId: Int, pollId: Int, optionId: Int) =
        executor.execute { api.unvotePoll(it, chatId, pollId, VoteRequest(optionId)) }

    override suspend fun deletePoll(chatId: Int, pollId: Int) = executor.execute { api.deletePoll(it, chatId, pollId) }
}

