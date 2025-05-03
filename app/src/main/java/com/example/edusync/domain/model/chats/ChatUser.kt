package com.example.edusync.domain.model.chats

import com.google.gson.annotations.SerializedName

data class ChatUser(
    @SerializedName("user_id")
    val id: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("is_teacher")
    val isTeacher: Boolean
)