package com.example.edusync.data.repository.group

import android.util.Log
import com.example.edusync.data.local.entities.GroupDao
import com.example.edusync.data.local.entities.GroupEntity
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.GroupResponse
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.repository.group.GroupRepository
import retrofit2.Response

class GroupRepositoryImpl(
    private val apiService: EduSyncApiService,
    private val groupDao: GroupDao
) : GroupRepository {

    override suspend fun getGroupsByInstitution(institutionId: Int): Result<List<Group>> {
        val localGroups = groupDao.getGroupsByInstitutionId(institutionId)
            ?.map { it.mapToDomain() }
            ?: emptyList()

        Log.d("SearchScreen","$localGroups")

        if (localGroups.isNotEmpty()) {
            return Result.success(localGroups)
        }

        return try {
            val response = apiService.getGroupsByInstitution(institutionId)
            handleResponse(response).map { it.mapToDomain() }
        } catch (e: Exception) {
            return Result.success(localGroups)
        }
    }

    override suspend fun getGroupById(groupId: Int): Result<Group> {
        return try {
            val response = apiService.getGroupById(groupId)
            handleResponse(response).map { it.mapToDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Server error: ${response.code()}"))
        }
    }

    private fun GroupResponse.mapToDomain(): Group {
        return Group(
            id = id ?: 0,
            name = name.orEmpty(),
            institutionId = institution_id ?: 0
        )
    }

    private fun List<GroupResponse>.mapToDomain(): List<Group> {
        return map { it.mapToDomain() }
    }

    suspend fun syncGroups(institutionId: Int) {
        val serverGroups = apiService.getGroupsByInstitution(institutionId)
            .body()
            ?.mapNotNull { it.mapToEntity() }
            ?: emptyList()

        if (serverGroups.isEmpty()) {
            Log.w("GroupSync", "Warning: Empty groups list from server")
            return
        }

        groupDao.deleteAll()
        groupDao.insertAll(serverGroups)
    }



    private fun GroupEntity.mapToDomain(): Group {
        return Group(
            id = id,
            name = name,
            institutionId = institutionId
        )
    }

    private fun GroupResponse.mapToEntity(): GroupEntity? {
        if (id == null || name == null || institution_id == null) {
            Log.e("GroupMapping", "Invalid group: id=$id, name=$name, institution_id=$institution_id")
            return null
        }
        return GroupEntity(
            id = id,
            name = name,
            institutionId = institution_id
        )
    }

}