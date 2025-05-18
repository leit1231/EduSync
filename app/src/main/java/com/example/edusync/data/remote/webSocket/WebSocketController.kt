package com.example.edusync.data.remote.webSocket

import android.util.Log
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import org.json.JSONObject

object WebSocketController {

    private val activeViewModels = mutableMapOf<Int, GroupViewModel>()
    private val messageBuffer = mutableMapOf<Int, MutableList<String>>()
    private val messageIdToChatMap = mutableMapOf<Int, Int>()

    fun register(chatId: Int, viewModel: GroupViewModel) {
        Log.d("WebSocket", "REGISTER VM=$viewModel for chatId=$chatId")
        activeViewModels[chatId] = viewModel

        subscribeToChat(chatId)

        messageBuffer.remove(chatId)?.forEach { raw ->
            Log.d("WebSocket", "Replaying buffered message for chatId=$chatId: $raw")
            WebSocketEventHandler.handleBufferedMessage(raw)
        }
    }

    fun subscribeToChat(chatId: Int) {
        val message = JSONObject().apply {
            put("action", "subscribe")
            put("chat_id", chatId)
        }
        WebSocketManager.send(message.toString())
        Log.d("WebSocket", "SUBSCRIBE: $message")
    }

    fun unregister(chatId: Int) {
        activeViewModels.remove(chatId)
    }

    fun mapMessageIdToChat(messageId: Int, chatId: Int) {
        messageIdToChatMap[messageId] = chatId
    }

    fun findChatIdByMessageId(messageId: Int): Int? {
        val result = messageIdToChatMap[messageId]
        Log.d("WebSocket", "Lookup chatId by messageId=$messageId => $result")
        return result
    }

    fun getAllChatIds(): List<Int> {
        return activeViewModels.keys.toList()
    }

    fun getViewModel(chatId: Int): GroupViewModel? {
        return activeViewModels[chatId]
    }

    fun bufferMessage(chatId: Int, raw: String) {
        val list = messageBuffer.getOrPut(chatId) { mutableListOf() }
        list.add(raw)
        Log.d("WebSocket", "Message buffered for chatId=$chatId: $raw")
    }
}