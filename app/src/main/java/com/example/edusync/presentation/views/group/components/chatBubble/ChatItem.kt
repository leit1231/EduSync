package com.example.edusync.presentation.views.group.components.chatBubble

import com.example.edusync.domain.model.message.Message

sealed class ChatItem {
    abstract val timestamp: String

    data class MessageItem(val message: Message) : ChatItem() {
        override val timestamp: String get() = message.timestamp
    }
}
