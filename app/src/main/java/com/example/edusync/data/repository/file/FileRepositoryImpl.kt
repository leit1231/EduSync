package com.example.edusync.data.repository.file

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.repository.file.FileRepository
import okhttp3.ResponseBody
import retrofit2.Response

class FileRepositoryImpl(
    private val api: EduSyncApiService,
    prefs: EncryptedSharedPreference
) : FileRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun downloadFileById(fileId: Int): Result<Response<ResponseBody>> {
        return executor.executeRawResponse { api.downloadFileById(it, fileId) }
    }

}
