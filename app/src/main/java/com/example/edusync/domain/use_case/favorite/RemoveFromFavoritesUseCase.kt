package com.example.edusync.domain.use_case.favorite

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.favorite.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoveFromFavoritesUseCase(
    private val repository: FavoriteRepository
) {
    operator fun invoke(fileId: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val result = repository.removeFromFavorites(fileId)
        emit(result.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error("Ошибка удаления из избранного", null) }
        ))
    }.flowOn(Dispatchers.IO)
}
