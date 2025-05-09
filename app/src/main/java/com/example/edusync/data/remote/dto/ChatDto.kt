package com.example.edusync.data.remote.dto

data class ChatResponse(
    val id: Int,
    val group_id: Int,
    val owner_id: Int,
    val owner_full_name: String,
    val subject_id: Int,
    val subject_name: String
)

data class CreateUpdateChatResponse(
    val id: Int,
    val group_id: Int,
    val owner_id: Int,
    val subject_id: Int,
    val join_code: String,
    val invite_link: String,
    val created_at: String
)

data class CreateChatResponse(
    val chat_info: CreateUpdateChatResponse,
    val message: String
)

data class RefreshInviteCodeResponse(
    val chat: CreateUpdateChatResponse,
    val message: String
)

data class ChatUser(
    val user_id: Int,
    val full_name: String,
    val is_teacher: Boolean
)

data class CreateChatRequest(
    val group_id: Int,
    val subject_id: Int
)

data class JoinByInviteRequest(
    val code: String
)