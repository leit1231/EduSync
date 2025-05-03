package com.example.edusync.domain.model.chats

data class ChatInfo(
    val id: Int,
    val groupId: Int,
    val subjectId: Int,
    val subjectName: String,
    val ownerName: String
)
