package com.example.edusync.domain.use_case.institution

import com.example.edusync.common.Resource
import com.example.edusync.domain.model.institution.Institute
import com.example.edusync.domain.repository.institution.InstituteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetAllInstitutesUseCase(
    private val repository: InstituteRepository
) {
    operator fun invoke():Flow<Resource<List<Institute>>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.getAllInstitutes()

            if (result.isSuccess){
                val institutions = result.getOrNull()
                emit(Resource.Success(institutions))
            }else{
                emit(Resource.Error("Ошибка загрузки учебных заведений", null))
            }
        }catch (e:Exception){
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}