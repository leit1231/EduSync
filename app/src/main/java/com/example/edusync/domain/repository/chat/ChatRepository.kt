package com.example.edusync.domain.repository.chat

import com.example.edusync.data.remote.dto.ChatResponse
import com.example.edusync.data.remote.dto.CreateChatRequest
import com.example.edusync.data.remote.dto.CreateChatResponse
import com.example.edusync.data.remote.dto.RefreshInviteCodeResponse
import com.example.edusync.domain.model.chats.ChatUser

interface ChatRepository {
    suspend fun getChats(): Result<List<ChatResponse>>
    suspend fun createChat(request: CreateChatRequest): Result<CreateChatResponse>
    suspend fun deleteChat(chatId: Int): Result<Unit>
    suspend fun joinByInvite(inviteCode: String): Result<Unit>
    suspend fun leaveChat(chatId: Int): Result<Unit>
    suspend fun refreshInviteCode(chatId: Int): Result<RefreshInviteCodeResponse>
    suspend fun getParticipants(chatId: Int): Result<List<ChatUser>>
    suspend fun removeParticipant(chatId: Int, userId: Int): Result<Unit>
}