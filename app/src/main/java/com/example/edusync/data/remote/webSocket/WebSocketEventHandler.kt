package com.example.edusync.data.remote.webSocket

import android.content.Context
import android.util.Log
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.domain.model.message.FileAttachment
import com.example.edusync.domain.model.message.Message
import com.example.edusync.domain.model.message.PollData
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class WebSocketEventHandler(
    private val context: Context, // ⬅️ добавлено
    private val socketManager: WebSocketManager,
    private val groupViewModelProvider: (chatId: Int) -> GroupViewModel?
) {

    init {
        setInstance(this)

        CoroutineScope(Dispatchers.IO).launch {
            socketManager.incomingMessages.collectLatest { jsonString ->
                Log.d("WebSocket", "RECEIVED: $jsonString")
                handleIncomingMessage(jsonString)
            }
        }
    }

    fun handleIncomingMessage(raw: String) {
        try {
            val json = JSONObject(raw)
            val event = json.getString("event")
            val message = json.getJSONObject("data")
            val chatId = message.getInt("chat_id")
            val gson = Gson()

            val viewModel = groupViewModelProvider(chatId)
            if (viewModel == null) {
                Log.d("WebSocket", "No ViewModel yet for chatId=$chatId. Buffering...")
                WebSocketController.bufferMessage(chatId, raw)
                return
            }

            when (event) {
                "message:new" -> {
                    val messageDto = gson.fromJson(message.toString(), MessageDto::class.java)
                    viewModel.receiveMessageFromDto(messageDto, context)
                }

                "message:delete" -> {
                    val id = message.getInt("id")
                    viewModel.deleteMessageLocally(id)
                }

                "message:updated" -> {
                    val messageDto = gson.fromJson(message.toString(), MessageDto::class.java)
                    viewModel.updateMessageFromDto(messageDto)
                }

                "poll:new" -> {
                    val poll = message
                    val optionsArray = poll.getJSONArray("options")
                    val options = List(optionsArray.length()) { i ->
                        val opt = optionsArray.getJSONObject(i)
                        PollData.Option(
                            id = opt.getInt("id"),
                            text = opt.getString("text"),
                            votes = opt.getInt("votes")
                        )
                    }

                    val pollData = PollData(
                        id = poll.getInt("id"),
                        question = poll.getString("question"),
                        createdAt = poll.getString("created_at"),
                        options = options
                    )

                    val senderName = resolveSenderName(viewModel.currentUserId, chatId)

                    viewModel.receiveMessage(
                        text = "", // потому что это не текст, а опрос
                        sender = senderName,
                        userId = viewModel.currentUserId,
                        files = emptyList(), // нет файлов, только опрос
                        pollData = pollData // добавим параметр
                    )
                }

                "poll:delete" -> {
                    val pollId = message.getInt("id")
                    viewModel.removePollLocally(pollId)
                }
            }

        } catch (e: Exception) {
            Log.e("WebSocketEventHandler", "Error parsing message: ${e.message}", e)
        }
    }


    private fun parseFiles(array: JSONArray?): List<FileAttachment> {
        if (array == null) return emptyList()
        return List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            FileAttachment(
                uri = android.net.Uri.parse(obj.getString("uri")),
                fileName = obj.getString("fileName"),
                fileSize = obj.getString("fileSize")
            )
        }
    }

    private fun resolveSenderName(userId: Int, chatId: Int): String {
        return groupViewModelProvider(chatId)?.participants?.value
            ?.firstOrNull { it.id == userId }?.fullName ?: "Неизвестный"
    }

    companion object {
        private var instance: WebSocketEventHandler? = null

        fun setInstance(handler: WebSocketEventHandler) {
            instance = handler
        }

        fun handleBufferedMessage(raw: String) {
            instance?.handleIncomingMessage(raw)
        }
    }
}