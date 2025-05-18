package com.example.edusync.data.remote.webSocket

import android.content.Context
import android.util.Log
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.domain.model.message.PollData
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class WebSocketEventHandler(
    private val context: Context,
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
            val gson = Gson()

            when (event) {
                "message:new", "message:updated", "message:delete" -> {
                    var chatId = message.optInt("chat_id", -1)
                    if (chatId == -1 && event.startsWith("message")) {
                        val messageId = message.optInt("id", -1)
                        chatId = WebSocketController.findChatIdByMessageId(messageId) ?: -1
                    }

                    if (chatId == -1) {
                        Log.e("WebSocketEventHandler", "Unable to resolve chat_id in $event")
                        return
                    }

                    val viewModel = WebSocketController.getViewModel(chatId)
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

                        "message:updated" -> {
                            val messageDto = gson.fromJson(message.toString(), MessageDto::class.java)
                            viewModel.updateMessageFromDto(messageDto)
                        }

                        "message:delete" -> {
                            val id = message.getInt("id")
                            viewModel.deleteMessageLocally(id)
                        }
                    }
                }

                "poll:new" -> {
                    val createdAtIso = message.getString("created_at")
                    val optionsArray = message.getJSONArray("options")
                    val options = List(optionsArray.length()) { i ->
                        val opt = optionsArray.getJSONObject(i)
                        PollData.Option(
                            id = opt.getInt("id"),
                            text = opt.getString("text"),
                            votes = opt.getInt("votes")
                        )
                    }

                    val pollData = PollData(
                        id = message.getInt("id"),
                        question = message.getString("question"),
                        createdAt = formatTimestamp(createdAtIso),
                        timestampMillis = parseIsoTimestampToMillis(createdAtIso),
                        options = options
                    )

                    WebSocketController.getAllChatIds().forEach { chatId ->
                        val viewModel = WebSocketController.getViewModel(chatId)
                        val teacher = viewModel?.participants?.value?.firstOrNull { it.isTeacher }
                        if (viewModel != null && teacher != null) {
                            viewModel.receiveMessage(
                                text = "",
                                sender = teacher.fullName,
                                userId = teacher.id,
                                files = emptyList(),
                                pollData = pollData
                            )
                        }
                    }
                }

                "poll:delete" -> {
                    val pollId = message.getInt("id")

                    WebSocketController.getAllChatIds().forEach { chatId ->
                        val viewModel = WebSocketController.getViewModel(chatId)
                        viewModel?.removePollLocally(pollId)
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("WebSocketEventHandler", "Error parsing message: ${e.message}", e)
        }
    }

    private fun parseIsoTimestampToMillis(iso: String): Long {
        return try {
            val trimmed = iso
                .substringBefore("Z")
                .takeWhile { it != '.' } + "." +
                    iso.substringAfter(".", "000000").padEnd(6, '0').take(6) + "Z"

            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(trimmed)?.time ?: 0L
        } catch (e: Exception) {
            Log.e("WebSocketEventHandler", "Failed to parse millis from $iso", e)
            0L
        }
    }

    private fun formatTimestamp(isoString: String): String {
        return try {
            val trimmed = isoString
                .substringBefore("Z")
                .takeWhile { it != '.' } + "." +
                    isoString.substringAfter(".", "000000").padEnd(6, '0').take(6) + "Z"

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(trimmed) ?: return isoString

            val outputFormat = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            isoString
        }
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