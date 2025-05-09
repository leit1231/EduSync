package com.example.edusync.domain.model.message

import android.net.Uri

data class Message(
    val id: Int,
    val text: String?,
    val sender: String,
    val timestamp: String,
    val isMe: Boolean,
    val files: List<FileAttachment> = emptyList(),
    val showSenderName: Boolean = true,
    val isEdited: Boolean = false,
    val replyToMessageId: Int? = null,
    val pollData: PollData? = null
)

data class FileAttachment(
    val uri: Uri,
    val fileName: String,
    val fileSize: String
)

data class PollData(
    val id: Int,
    val question: String,
    val createdAt: String,
    val options: List<Option>,
    val selectedOption: Int? = null
) {
    val totalVotes: Int
        get() = options.sumOf { it.votes }

    data class Option(
        val id: Int,
        val text: String,
        val votes: Int
    )
}
