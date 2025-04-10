package com.example.edusync.domain.repository.group

import com.example.edusync.domain.model.group.Group

interface GroupRepository {
    suspend fun getGroupsByInstitution(institutionId: Int): Result<List<Group>>
    suspend fun getGroupById(groupId: Int): Result<Group>
}