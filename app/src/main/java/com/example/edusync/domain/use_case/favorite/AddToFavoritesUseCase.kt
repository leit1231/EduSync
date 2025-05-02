package com.example.edusync.domain.use_case.favorite

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.favorite.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AddToFavoritesUseCase(
    private val repository: FavoriteRepository
) {
    operator fun invoke(fileId: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val result = repository.addToFavorites(fileId)
        emit(result.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error("Ошибка добавления в избранное", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
