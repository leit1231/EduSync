package com.example.edusync.data.local.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InstituteDao {
    @Query("SELECT * FROM institutes")
    suspend fun getAllSuspend(): List<InstituteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(institutes: List<InstituteEntity>)

    @Query("DELETE FROM institutes")
    suspend fun deleteAll()
}

@Dao
interface GroupDao {

    @Query("SELECT * FROM groups WHERE institutionId = :institutionId")
    suspend fun getGroupsByInstitutionIdSuspend(institutionId: Int): List<GroupEntity>?

    @Query("SELECT * FROM groups WHERE institutionId = :institutionId")
    fun getGroupsByInstitutionId(institutionId: Int): List<GroupEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<GroupEntity>)

    @Query("DELETE FROM groups")
    suspend fun deleteAll()
}

@Dao
interface TeacherInitialsDao {
    @Query("SELECT * FROM teacher_initials")
    suspend fun getAll(): List<TeacherInitialsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teachers: List<TeacherInitialsEntity>)

    @Query("DELETE FROM teacher_initials")
    suspend fun deleteAll()
}

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedule WHERE groupId = :groupId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getGroupSchedule(groupId: Int): ScheduleEntity?

    @Query("SELECT * FROM schedule WHERE teacherId = :teacherId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getTeacherSchedule(teacherId: Int): ScheduleEntity?

    @Query("DELETE FROM schedule WHERE groupId = :groupId")
    suspend fun deleteGroupSchedule(groupId: Int)

    @Query("DELETE FROM schedule WHERE teacherId = :teacherId")
    suspend fun deleteTeacherSchedule(teacherId: Int)
}