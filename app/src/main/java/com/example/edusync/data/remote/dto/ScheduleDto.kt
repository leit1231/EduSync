package com.example.edusync.data.remote.dto

import com.example.edusync.domain.model.schedule.Day
import com.example.edusync.domain.model.schedule.PairInfo
import com.example.edusync.domain.model.schedule.PairItem
import com.example.edusync.domain.model.schedule.Schedule
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleItem(
    val id: Int,
    @SerializedName("group_id")
    val groupId: Int?,
    @SerializedName("subject")
    val subject: String?,
    @SerializedName("teacher_initials")
    val teacher: String?,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("classroom")
    val room: String?,
    val building: String? = null,
    val date: String,
    @SerializedName("day_of_week")
    val dayOfWeek: String? = "",
    @SerializedName("pair_number")
    val pairNumber: Int,
    val notice: String? = null
)

fun List<ScheduleItem>.toDomain(name: String = "") = Schedule(
    name = name,
    days = this.groupBy { it.date.split("T")[0] }.map { (date, items) ->
        Day(
            isoDateDay = date,
            day = items.firstOrNull()?.dayOfWeek ?: "",
            pairs = items
                .sortedBy { it.startTime }
                .map {
                    val (auditoria, corpus) = parseRoom(it.room ?: "")
                    PairItem(
                        time = "${it.startTime.split("T")[1].substring(0..4)} - ${it.endTime.split("T")[1].substring(0..4)}",
                        isoDateStart = "${date}T${it.startTime.split("T")[1]}",
                        isoDateEnd = "${date}T${it.endTime.split("T")[1]}",
                        pairInfo = listOf(
                            PairInfo(
                                doctrine = it.subject?: "",
                                teacher = it.teacher ?: "",
                                group = it.groupId.toString(),
                                auditoria = auditoria.replace("ауд. ", "").trim(),
                                corpus = corpus,
                                number = it.pairNumber,
                                start = it.startTime.split("T")[1].substring(0..4),
                                end = it.endTime.split("T")[1].substring(0..4),
                                warn = it.notice ?: ""
                            )
                        )
                    )
                }
        )
    }
)


private fun parseRoom(room: String): Pair<String, String> {
    val parts = room.split("/").map { it.trim() }
    return if (parts.size > 1) {
        val auditoria = parts.dropLast(1).joinToString("/")
        val corpus = parts.last()
        auditoria to corpus
    } else {
        room to ""
    }
}