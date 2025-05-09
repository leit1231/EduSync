package com.example.edusync.data.remote.webSocket

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

object WebSocketManager {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var token: String? = null

    val incomingMessages = MutableSharedFlow<String>(extraBufferCapacity = 100)

    var isConnected = false
        private set

    fun connect(token: String) {
        if (isConnected || webSocket != null) return
        this.token = token

        val request = Request.Builder()
            .url("ws://192.168.0.28:8080/api/ws")
            .addHeader("Authorization", token)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                isConnected = true
                Log.d("WebSocket", "Connected")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                Log.e("WebSocket", "Error: ${t.message}", t)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("WebSocket", "MESSAGE: $text")
                incomingMessages.tryEmit(text)
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                isConnected = false
                ws.close(1000, null)
            }
        })
    }

    fun subscribe(chatId: Int) {
        val json = JSONObject()
            .put("action", "subscribe")
            .put("chat_id", chatId)
            .toString()
        Log.d("WebSocket", "SUBSCRIBE: $json")
        webSocket?.send(json)
    }

    fun sendMessage(chatId: Int, text: String) {
        val json = JSONObject()
            .put("action", "send")
            .put("chat_id", chatId)
            .put("text", text)
            .toString()
        webSocket?.send(json)
    }

    fun send(raw: String) {
        webSocket?.send(raw)
    }

    fun disconnect() {
        webSocket?.close(1000, null)
        webSocket = null
    }
}