package com.example.edusync.data.repository.message

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.EditMessageResponse
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.data.remote.dto.MessageResponse
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.repository.message.MessageRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MessageRepositoryImpl(
    private val api: EduSyncApiService,
    prefs: EncryptedSharedPreference
) : MessageRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun sendMessage(
        chatId: Int,
        text: String,
        files: List<MultipartBody.Part>
    ): Result<MessageResponse> = executor.execute { token ->
        val textPart = text.toRequestBody("text/plain".toMediaTypeOrNull())
        api.sendMessage(token, chatId, textPart, files)
    }

    override suspend fun getMessages(chatId: Int, limit: Int, offset: Int): Result<List<MessageDto>> {
        return executor.execute {
            api.getMessages(chatId = chatId, token = it, limit = limit, offset = offset)
        }
    }
    override suspend fun replyToMessage(chatId: Int, messageId: Int, text: String): Result<MessageResponse> {
        val textPart = text.toRequestBody("text/plain".toMediaTypeOrNull())
        val fileParts = emptyList<MultipartBody.Part>()

        return executor.execute { token ->
            api.replyMessage(token, chatId, messageId, textPart, fileParts)
        }
    }

    override suspend fun deleteMessage(chatId: Int, messageId: Int) =
        executor.execute { api.deleteMessage(it, chatId, messageId) }

    override suspend fun searchMessages(chatId: Int, query: String, limit: Int, offset: Int) =
        executor.execute { api.searchMessages(it, chatId, query, limit, offset) }

    override suspend fun editMessage(chatId: Int, messageId: Int, newText: String): Result<EditMessageResponse> =
        executor.execute {
            api.editMessage(it, chatId, messageId, mapOf("text" to newText))
        }
}

