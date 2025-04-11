package com.example.edusync.data.local.entities

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [InstituteEntity::class, GroupEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun instituteDao(): InstituteDao
    abstract fun groupDao(): GroupDao
}

fun <T> areListsEqual(
    serverList: List<T>,
    localList: List<T>
): Boolean {
    return serverList.size == localList.size && serverList.containsAll(localList)
}