package com.example.edusync.data.repository.institution

import android.util.Log
import androidx.room.Transaction
import androidx.room.withTransaction
import com.example.edusync.data.local.entities.AppDatabase
import com.example.edusync.data.local.entities.InstituteDao
import com.example.edusync.data.local.entities.InstituteEntity
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.InstituteResponse
import com.example.edusync.domain.model.institution.Institute
import com.example.edusync.domain.repository.institution.InstituteRepository
import retrofit2.Response

class InstituteRepositoryImpl(
    private val apiService: EduSyncApiService,
    private val instituteDao: InstituteDao,
    private val db: AppDatabase
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
        val localInstitutes = instituteDao.getAllSuspend().map { it.mapToDomain() }

        if (localInstitutes.isNotEmpty()) {
            return Result.success(localInstitutes)
        }

        return try {
            val response = apiService.getAllInstitutions()
            handleResponse(response).map { it.mapToDomain() }
        } catch (e: Exception) {
            Result.success(localInstitutes)
        }
    }

    suspend fun syncInstitutes() {
        val serverInstitutes = apiService.getAllInstitutions().body()
            ?.mapNotNull { it.mapToEntity() } // <-- Заменил на mapNotNull
            ?: emptyList()

        if (serverInstitutes.isEmpty()) {
            println("Warning: Empty institute list from server")
            return
        }

        instituteDao.deleteAll()
        instituteDao.insertAll(serverInstitutes)
    }

    private fun InstituteResponse.mapToEntity(): InstituteEntity? {
        if (id == null || name == null) {
            Log.e("InstituteMapping", "Invalid institute: id=$id, name=$name")
            return null
        }
        return InstituteEntity(
            id = id,
            name = name
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
        requireNotNull(id) { "InstituteResponse.id is null" }
        requireNotNull(name) { "InstituteResponse.name is null" }
        return Institute(
            id = id,
            name = name
        )
    }


    private fun List<InstituteResponse>.mapToDomain(): List<Institute> {
        return map { it.mapToDomain() }
    }
}