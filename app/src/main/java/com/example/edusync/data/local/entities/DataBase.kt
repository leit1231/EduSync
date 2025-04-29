package com.example.edusync.data.local.entities

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [InstituteEntity::class, GroupEntity::class, TeacherInitialsEntity::class, ScheduleEntity::class, ReminderEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun instituteDao(): InstituteDao
    abstract fun groupDao(): GroupDao
    abstract fun teacherInitialsDao(): TeacherInitialsDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun reminderDao(): ReminderDao
}