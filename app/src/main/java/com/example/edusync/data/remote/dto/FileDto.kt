package com.example.edusync.data.remote.dto

data class FileDto(
    val id: Int,
    val name: String,
    val url: String,
    val uploaded_at: String,
    val uploaded_by: Int
)
