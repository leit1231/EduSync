package com.example.edusync.domain.use_case.institution

import com.example.edusync.common.Resource
import com.example.edusync.domain.model.institution.Institute
import com.example.edusync.domain.repository.institution.InstituteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetInstituteByIdUseCase(
    private val repository: InstituteRepository
) {
    operator fun invoke(
        id: Int
    ): Flow<Resource<Institute>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.getInstituteById(id)

            if (result.isSuccess){
                val institute = result.getOrNull()
                emit(Resource.Success(institute))
            }else{
                emit(Resource.Error("Ошибка получения учебного заведения", null))
            }
        }catch (e:Exception){
            emit(Resource.Error(e.message ?: "Неизвестная ошибка", null))
        }
    }.flowOn(Dispatchers.IO)
}