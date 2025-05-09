package com.example.edusync.domain.repository.message

import com.example.edusync.data.remote.dto.EditMessageResponse
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.data.remote.dto.MessageResponse
import okhttp3.MultipartBody

interface MessageRepository {
    suspend fun sendMessage(chatId: Int, text: String, files: List<MultipartBody.Part>): Result<MessageResponse>
    suspend fun getMessages(chatId: Int, limit: Int, offset: Int): Result<List<MessageDto>>
    suspend fun replyToMessage(chatId: Int, messageId: Int, text: String): Result<MessageResponse>
    suspend fun deleteMessage(chatId: Int, messageId: Int): Result<MessageResponse>
    suspend fun searchMessages(chatId: Int, query: String, limit: Int, offset: Int): Result<List<MessageDto>>
    suspend fun editMessage(chatId: Int, messageId: Int, newText: String): Result<EditMessageResponse>
}
