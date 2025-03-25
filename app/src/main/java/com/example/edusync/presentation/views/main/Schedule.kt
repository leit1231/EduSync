package com.example.edusync.presentation.views.main

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
)

data class PairInfo(
    val doctrine: String,
    val teacher: String,
    val auditoria: String,
    val corpus: String,
    val number: Int,
    val start: String,
    val end: String,
    val warn: String,
)