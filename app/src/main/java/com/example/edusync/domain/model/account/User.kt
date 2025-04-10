package com.example.edusync.domain.model.account

data class User(
    val id: Int,
    val email: String,
    val fullName: String,
    val isTeacher: Boolean
)
