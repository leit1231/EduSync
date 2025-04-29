package com.example.edusync.data.repository.schedule

import com.example.edusync.data.local.entities.ReminderDao
import com.example.edusync.data.local.entities.ReminderEntity

class ReminderRepository(private val reminderDao: ReminderDao) {

    suspend fun saveReminder(
        isoDateTime: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        text: String
    ) {
        val reminder = ReminderEntity(
            groupId = groupId,
            teacherId = teacherId,
            isoDateTime = isoDateTime,
            text = text
        )
        reminderDao.insert(reminder)
    }

    suspend fun getReminders(groupId: Int?, teacherId: Int?): Map<String, String> {
        val reminders = when {
            groupId != null -> reminderDao.getByGroupId(groupId)
            teacherId != null -> reminderDao.getByTeacherId(teacherId)
            else -> emptyList()
        }
        return reminders.associateBy({ it.isoDateTime }, { it.text })
    }
}
