package com.example.edusync.domain.repository.institution

import com.example.edusync.domain.model.institution.Institute

interface InstituteRepository {
    suspend fun getInstituteById(id: Int): Result<Institute>
    suspend fun getAllInstitutes(): Result<List<Institute>>
    suspend fun getMaskedInstitutes(): Result<List<Institute>>
}