package com.example.edusync.data.repository.institution

import com.example.edusync.data.local.entities.InstituteDao
import com.example.edusync.data.local.entities.InstituteEntity
import com.example.edusync.data.local.entities.areListsEqual
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.InstituteResponse
import com.example.edusync.domain.model.institution.Institute
import com.example.edusync.domain.repository.institution.InstituteRepository
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response

class InstituteRepositoryImpl(
    private val apiService: EduSyncApiService,
    private val instituteDao: InstituteDao
) : InstituteRepository {

    override suspend fun getInstituteById(id: Int): Result<Institute> {
        return try {
            val response = apiService.getInstitutionById(id)
            handleResponse(response).map { it.mapToDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllInstitutes(): Result<List<Institute>> {
        val localInstitutes = instituteDao.getAll()
            .firstOrNull()
            ?.map { it.mapToDomain() }
            ?: emptyList()

        if (localInstitutes.isNotEmpty()) {
            return Result.success(localInstitutes)
        }

        return try {
            val response = apiService.getAllInstitutions()
            handleResponse(response).map { it.mapToDomain() }
        } catch (e: Exception) {
            return Result.success(localInstitutes)
        }
    }

    suspend fun syncInstitutes() {
        val serverInstitutes = apiService.getAllInstitutions().body()?.map { it.mapToEntity() } ?: emptyList()
        val localInstitutes = instituteDao.getAll().firstOrNull() ?: emptyList()

        if (!areListsEqual(serverInstitutes, localInstitutes)) {
            instituteDao.deleteAll()
            instituteDao.insertAll(serverInstitutes)
        }
    }

    private fun InstituteResponse.mapToEntity(): InstituteEntity {
        return InstituteEntity(
            id = ID,
            name = Name
        )
    }

    override suspend fun getMaskedInstitutes(): Result<List<Institute>> {
        return try {
            val response = apiService.getMaskedInstitutions()
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

    private fun InstituteEntity.mapToDomain(): Institute {
        return Institute(
            id = id,
            name = name
        )
    }

    private fun InstituteResponse.mapToDomain(): Institute {
        return Institute(
            id = ID,
            name = Name
        )
    }

    private fun List<InstituteResponse>.mapToDomain(): List<Institute> {
        return map { it.mapToDomain() }
    }
}