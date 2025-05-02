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
    override suspend fun addToFavorites(fileId: Int) = executor.execute { api.addToFavorites(it, fileId) }
    override suspend fun removeFromFavorites(fileId: Int) = executor.execute { api.removeFromFavorites(it, fileId) }
}

