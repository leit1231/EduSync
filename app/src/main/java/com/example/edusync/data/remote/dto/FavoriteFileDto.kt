package com.example.edusync.data.remote.dto

data class FavoriteFileDto(
    val id: Int,
    val name: String,
    val url: String,
    val uploaded_by: Int,
    val uploaded_at: String
)
