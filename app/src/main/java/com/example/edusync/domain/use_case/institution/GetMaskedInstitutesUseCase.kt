package com.example.edusync.domain.use_case.institution

import com.example.edusync.common.Resource
import com.example.edusync.domain.model.institution.Institute
import com.example.edusync.domain.repository.institution.InstituteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetMaskedInstitutesUseCase(
    private val repository: InstituteRepository
) {
    operator fun invoke():Flow<Resource<List<Institute>>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.getMaskedInstitutes()

            if (result.isSuccess){
                val mask = result.getOrNull()
                emit(Resource.Success(mask))
            }else{
                emit(Resource.Error("Ошибка получения масок", null))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}