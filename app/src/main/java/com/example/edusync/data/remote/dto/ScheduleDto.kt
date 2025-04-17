package com.example.edusync.data.remote.dto

import com.example.edusync.domain.model.schedule.Day
import com.example.edusync.domain.model.schedule.PairInfo
import com.example.edusync.domain.model.schedule.PairItem
import com.example.edusync.domain.model.schedule.Schedule

data class ScheduleResponse(
    val schedule: List<ScheduleItem>,
    val group_name: String
)

data class ScheduleItem(
    val id: Int,
    val subject: String,
    val teacher: String,
    val start_time: String,
    val end_time: String,
    val room: String,
    val building: String,
    val date: String,
    val day_of_week: String,
    val pair_number: Int,
    val notice: String?
)

fun ScheduleResponse.toDomain() = Schedule(
    name = group_name,
    days = schedule.groupBy { it.date }.map { (date, items) ->
        Day(
            isoDateDay = date,
            day = items.first().day_of_week,
            pairs = items.map {
                PairItem(
                    time = "${it.start_time} - ${it.end_time}",
                    isoDateStart = "$date ${it.start_time}:00",
                    isoDateEnd = "$date ${it.end_time}:00",
                    pairInfo = listOf(
                        PairInfo(
                            doctrine = it.subject,
                            teacher = it.teacher,
                            auditoria = it.room,
                            corpus = it.building,
                            number = it.pair_number,
                            start = it.start_time,
                            end = it.end_time,
                            warn = it.notice ?: ""
                        )
                    )
                )
            }.sortedBy { it.isoDateStart }
        )
    }
)