package com.example.edusync.data.local.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InstituteDao {
    @Query("SELECT * FROM institutes")
    fun getAll(): Flow<List<InstituteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(institutes: List<InstituteEntity>)

    @Query("DELETE FROM institutes")
    suspend fun deleteAll()
}

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups WHERE institutionId = :institutionId")
    fun getGroupsByInstitutionIdAsFlow(institutionId: Int): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE institutionId = :institutionId")
    fun getGroupsByInstitutionId(institutionId: Int): List<GroupEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<GroupEntity>)

    @Query("DELETE FROM groups")
    suspend fun deleteAll()
}