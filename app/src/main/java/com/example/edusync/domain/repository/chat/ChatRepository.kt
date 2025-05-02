package com.example.edusync.domain.repository.chat

import com.example.edusync.data.remote.dto.ChatResponse
import com.example.edusync.data.remote.dto.CreateChatRequest
import com.example.edusync.data.remote.dto.InviteToChatRequest
import com.example.edusync.domain.model.account.User

interface ChatRepository {
    suspend fun getChats(): Result<List<ChatResponse>>
    suspend fun createChat(request: CreateChatRequest): Result<Unit>
    suspend fun deleteChat(chatId: Int): Result<Unit>
    suspend fun addUserToChat(chatId: Int, userId: Int): Result<Unit>
    suspend fun inviteToChat(request: InviteToChatRequest): Result<Unit>
    suspend fun joinByInvite(inviteCode: String): Result<Unit>
    suspend fun leaveChat(chatId: Int): Result<Unit>
    suspend fun refreshInviteCode(chatId: Int): Result<Unit>
    suspend fun getParticipants(chatId: Int): Result<List<User>>
    suspend fun removeParticipant(chatId: Int, userId: Int): Result<Unit>
}