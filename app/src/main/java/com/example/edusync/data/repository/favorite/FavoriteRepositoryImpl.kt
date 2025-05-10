package com.example.edusync.data.repository.favorite

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.repository.favorite.FavoriteRepository

class FavoriteRepositoryImpl(
    private val api: EduSyncApiService,
    prefs: EncryptedSharedPreference
) : FavoriteRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun getFavorites() = executor.execute { api.getFavorites(it) }
    override suspend fun addToFavorites(fileId: Int): Result<Unit> {
        return executor.executeNoContent { token -> api.addToFavorites(token, fileId) }
    }
    override suspend fun removeFromFavorites(fileId: Int): Result<Unit> {
        return executor.executeNoContent { token -> api.removeFavorite(token, fileId) }
    }
}

