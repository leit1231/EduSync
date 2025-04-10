package com.example.edusync.data.repository.group

import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.GroupResponse
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.repository.group.GroupRepository
import retrofit2.Response

class GroupRepositoryImpl(
    private val apiService: EduSyncApiService
) : GroupRepository {

    override suspend fun getGroupsByInstitution(institutionId: Int): Result<List<Group>> {
        return try {
            val response = apiService.getGroupsByInstitution(institutionId)
            handleResponse(response).map { it.mapToDomain() }
        } catch (e: Exception) {
            Result.failure(e)
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
            id = ID,
            name = Name,
            institutionId = InstitutionID
        )
    }

    private fun List<GroupResponse>.mapToDomain(): List<Group> {
        return map { it.mapToDomain() }
    }
}
