package com.example.edusync.data.remote.dto

data class CreatePollRequest(
    val question: String,
    val options: List<String>
)

data class VoteRequest(
    val option_id: Int
)

data class PollDto(
    val id: Int,
    val question: String,
    val created_at: String,
    val options: List<PollOptionDto>,
)

data class PollOptionDto(
    val id: Int,
    val text: String,
    val votes: Int
)

data class PollResponse(
    val poll_id: Int
)

data class OptionDto(
    val id: Int,
    val text: String,
    val votes: Int
)
