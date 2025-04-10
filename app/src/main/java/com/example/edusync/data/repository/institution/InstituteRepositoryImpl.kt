package com.example.edusync.data.repository.institution

import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.InstituteResponse
import com.example.edusync.domain.model.institution.Institute
import com.example.edusync.domain.repository.institution.InstituteRepository
import retrofit2.Response

class InstituteRepositoryImpl(
    private val apiService: EduSyncApiService
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
        return try {
            val response = apiService.getAllInstitutions()
            handleResponse(response).map { it.mapToDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
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