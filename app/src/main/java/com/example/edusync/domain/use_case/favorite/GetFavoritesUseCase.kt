package com.example.edusync.domain.use_case.favorite

import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.FavoriteFileDto
import com.example.edusync.domain.repository.favorite.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetFavoritesUseCase(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<Resource<List<FavoriteFileDto>>> = flow {
        emit(Resource.Loading())
        val result = repository.getFavorites()
        val resource = result.fold(
            onSuccess = { favorites ->
                Resource.Success(favorites ?: emptyList())
            },
            onFailure = { error ->
                if (error.message?.contains("Пустое тело ответа") == true) {
                    Resource.Success(emptyList())
                } else {
                    Resource.Error("Ошибка загрузки избранного", emptyList())
                }
            }
        )
        emit(resource)
    }.flowOn(Dispatchers.IO)
}
