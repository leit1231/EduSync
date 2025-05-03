package com.example.edusync.domain.repository.message

import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.data.remote.dto.MessageResponse
import java.io.File

interface MessageRepository {
    suspend fun sendMessage(chatId: Int, text: String, files: List<File>): Result<MessageResponse>
    suspend fun getMessages(chatId: Int): Result<List<MessageDto>>
    suspend fun replyToMessage(chatId: Int, messageId: Int, text: String): Result<MessageResponse>
    suspend fun deleteMessage(chatId: Int, messageId: Int): Result<MessageResponse>
    suspend fun searchMessages(chatId: Int, query: String, limit: Int, offset: Int): Result<List<MessageDto>>
}
