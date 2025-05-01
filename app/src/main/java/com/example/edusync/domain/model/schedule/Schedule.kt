package com.example.edusync.domain.model.schedule

data class Schedule(
    val days: List<Day>,
    val name: String,
)

data class Day(
    val pairs: List<PairItem>,
    val isoDateDay: String,
    val day: String,
)

data class PairItem(
    val time: String,
    val pairInfo: List<PairInfo>,
    val isoDateStart: String,
    val isoDateEnd: String,
    val scheduleId: Int? = null
)


data class PairInfo(
    val doctrine: String,
    val teacher: String,
    val group: String,
    val auditoria: String,
    val corpus: String,
    val number: Int,
    val start: String,
    val end: String,
    val warn: String,
)

fun Schedule.withReminders(reminders: Map<String, String>): Schedule {
    return this.copy(
        days = days.map { day ->
            day.copy(pairs = day.pairs.map { pair ->
                pair.copy(pairInfo = pair.pairInfo.map {
                    it.copy(warn = reminders[pair.isoDateStart] ?: "")
                })
            })
        }
    )
}
