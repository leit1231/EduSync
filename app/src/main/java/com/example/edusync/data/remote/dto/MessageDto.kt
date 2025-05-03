package com.example.edusync.data.remote.dto

data class MessageDto(
    val id: Int,
    val chat_id: Int,
    val user_id: Int,
    val message_group_id: Int,
    val parent_message_id: Int,
    val text: String,
    val created_at: String,
    val files: List<Files>
)

data class MessageResponse(
    val message_id: Int
)

data class Files(
    val id: Int,
    val file_url: String
)