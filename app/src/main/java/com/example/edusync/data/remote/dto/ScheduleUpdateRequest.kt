package com.example.edusync.data.remote.dto

data class ScheduleUpdateRequest(
    val group_id: Int,
    val subject_id: Int,
    val date: String,
    val pair_number: Int,
    val classroom: String,
    val teacher_initials_id: Int,
    val start_time: String,
    val end_time: String
)