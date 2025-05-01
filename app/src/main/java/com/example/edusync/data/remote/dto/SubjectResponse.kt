package com.example.edusync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectResponse(
    val id: Int,
    val name: String
)