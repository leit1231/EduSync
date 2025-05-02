package com.example.edusync.domain.use_case.file

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.FileDto
import com.example.edusync.domain.repository.file.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetFileByIdUseCase(
    private val repository: FileRepository
) {
    operator fun invoke(fileId: Int): Flow<Resource<FileDto>> = flow {
        emit(Resource.Loading())
        val result = repository.getFileById(fileId)
        emit(result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error("Ошибка получения файла", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
