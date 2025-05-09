package com.example.edusync.domain.repository.poll

import com.example.edusync.data.remote.dto.CreatePollRequest
import com.example.edusync.data.remote.dto.PollDto
import com.example.edusync.data.remote.dto.PollResponse

interface PollRepository {
    suspend fun getPolls(chatId: Int): Result<List<PollDto>>
    suspend fun createPoll(chatId: Int, request: CreatePollRequest): Result<PollResponse>
    suspend fun vote(chatId: Int, pollId: Int, optionId: Int): Result<Unit>
    suspend fun unvote(chatId: Int, pollId: Int, optionId: Int): Result<Unit>
    suspend fun deletePoll(chatId: Int, pollId: Int): Result<Unit>
}
