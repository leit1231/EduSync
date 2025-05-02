package com.example.edusync.data.remote.dto

data class ChatResponse(
    val id: Int,
    val name: String,
    val participants: List<Int>,
    val createdAt: String
)

data class CreateChatRequest(
    val name: String,
    val user_ids: List<Int>
)

data class AddUserToChatRequest(
    val chat_id: Int,
    val user_id: Int
)

data class InviteToChatRequest(
    val chat_id: Int,
    val email: String
)

data class JoinByInviteRequest(
    val invite_code: String
)

data class LeaveChatRequest(
    val chat_id: Int
)
