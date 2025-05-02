package com.example.edusync.domain.repository.favorite

import com.example.edusync.data.remote.dto.FavoriteFileDto

interface FavoriteRepository {
    suspend fun getFavorites(): Result<List<FavoriteFileDto>>
    suspend fun addToFavorites(fileId: Int): Result<Unit>
    suspend fun removeFromFavorites(fileId: Int): Result<Unit>
}
