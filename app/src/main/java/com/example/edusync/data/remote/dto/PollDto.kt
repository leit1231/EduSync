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
    val options: List<OptionDto>,
    val voted_option_id: Int?
)

data class OptionDto(
    val id: Int,
    val text: String,
    val votes: Int
)
