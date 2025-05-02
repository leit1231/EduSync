package com.example.edusync.data.repository.file

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.repository.file.FileRepository

class FileRepositoryImpl(
    private val api: EduSyncApiService,
    prefs: EncryptedSharedPreference
) : FileRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun getFileById(fileId: Int) = executor.execute { api.getFileById(it, fileId) }
}

