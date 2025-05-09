package com.example.edusync.domain.repository.file

import okhttp3.ResponseBody
import retrofit2.Response

interface FileRepository {
    suspend fun downloadFileById(fileId: Int): Result<Response<ResponseBody>>
}