package com.example.edusync.domain.model.chats

import com.example.edusync.data.remote.dto.ChatResponse

data class ChatInfo(
    val id: Int,
    val groupId: Int,
    val subjectId: Int,
    val subjectName: String,
    val ownerName: String
)

fun ChatResponse.toChatInfo(): ChatInfo {
    return ChatInfo(
        id = this.id,
        groupId = this.group_id,
        subjectId = this.subject_id,
        subjectName = this.subject_name,
        ownerName = this.owner_full_name
    )
}