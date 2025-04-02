package com.example.edusync.presentation.views.main.component.dateItem

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun String.toCalendar(): Calendar {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf.parse(this) ?: throw IllegalArgumentException("Invalid date format")
    return Calendar.getInstance().apply { time = date }
}

fun Calendar.toFullMonth(): String {
    return SimpleDateFormat("MMMM", Locale.getDefault()).format(time)
        .replaceFirstChar { it.uppercase() }
}

fun Calendar.toDayOfWeek(): String {
    return SimpleDateFormat("EEEE", Locale.getDefault()).format(time)
        .replaceFirstChar { it.uppercase() }
}
