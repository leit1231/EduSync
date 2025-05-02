package com.example.edusync.data.repository.message

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
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

    override suspend fun sendMessage(chatId: Int, text: String, files: List<File>): Result<Unit> =
        executor.execute { token ->
            val textPart = text.toRequestBody("text/plain".toMediaTypeOrNull())
            val fileParts = files.map {
                val req = it.asRequestBody("application/octet-stream".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("files", it.name, req)
            }
            api.sendMessage(token, chatId, textPart, fileParts)
        }

    override suspend fun getMessages(chatId: Int) = executor.execute { api.getMessages(it, chatId) }
    override suspend fun replyToMessage(chatId: Int, messageId: Int, text: String) =
        executor.execute { api.replyMessage(it, chatId, messageId, mapOf("text" to text)) }

    override suspend fun deleteMessage(chatId: Int, messageId: Int) =
        executor.execute { api.deleteMessage(it, chatId, messageId) }

    override suspend fun searchMessages(chatId: Int, query: String, limit: Int, offset: Int) =
        executor.execute { api.searchMessages(it, chatId, query, limit, offset) }
}

