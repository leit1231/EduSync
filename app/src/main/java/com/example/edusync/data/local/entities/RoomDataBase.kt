package com.example.edusync.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "institutes")
data class InstituteEntity(
    @PrimaryKey val id: Int,
    val name: String
)

@Entity(
    tableName = "groups",
    foreignKeys = [
        ForeignKey(
            entity = InstituteEntity::class,
            parentColumns = ["id"],
            childColumns = ["institutionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GroupEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val institutionId: Int
)