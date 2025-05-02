package com.example.edusync.data.repository.chat

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.AddUserToChatRequest
import com.example.edusync.data.remote.dto.CreateChatRequest
import com.example.edusync.data.remote.dto.InviteToChatRequest
import com.example.edusync.data.remote.dto.JoinByInviteRequest
import com.example.edusync.data.remote.dto.LeaveChatRequest
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.repository.chat.ChatRepository

class ChatRepositoryImpl(
    private val api: EduSyncApiService,
    prefs: EncryptedSharedPreference
) : ChatRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun getChats() = executor.execute { api.getChats(it) }
    override suspend fun createChat(request: CreateChatRequest) = executor.execute { api.createChat(it, request) }
    override suspend fun deleteChat(chatId: Int) = executor.execute { api.deleteChat(it, chatId) }
    override suspend fun addUserToChat(chatId: Int, userId: Int) =
        executor.execute { api.addUserToChat(it, chatId, AddUserToChatRequest(chatId, userId)) }

    override suspend fun inviteToChat(request: InviteToChatRequest) = executor.execute { api.inviteToChat(it, request) }
    override suspend fun joinByInvite(inviteCode: String) = executor.execute { api.joinChatByInvite(it, JoinByInviteRequest(inviteCode)) }
    override suspend fun leaveChat(chatId: Int) = executor.execute { api.leaveChat(it, LeaveChatRequest(chatId)) }
    override suspend fun refreshInviteCode(chatId: Int) = executor.execute { api.refreshInviteCode(it, chatId) }
    override suspend fun getParticipants(chatId: Int) = executor.execute { api.getChatParticipants(it, chatId) }
    override suspend fun removeParticipant(chatId: Int, userId: Int) = executor.execute { api.removeChatParticipant(it, chatId, userId) }
}
