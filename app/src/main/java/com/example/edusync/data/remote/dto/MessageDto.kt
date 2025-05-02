package com.example.edusync.data.remote.dto

data class MessageDto(
    val id: Int,
    val sender_id: Int,
    val text: String,
    val created_at: String,
    val reply_to: Int?,
    val file_urls: List<String>
)
