package com.example.edusync.domain.use_case.file

import com.example.edusync.common.Resource
import com.example.edusync.domain.repository.file.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.URLDecoder


class GetFileByIdUseCase(
    private val repository: FileRepository
) {
    operator fun invoke(fileId: Int, fileUrl: String? = null): Flow<Resource<Pair<ResponseBody, String?>>> = flow {
        emit(Resource.Loading())

        val result: Result<Response<ResponseBody>> = repository.downloadFileById(fileId)

        emit(result.fold(
            onSuccess = { response ->
                val body = response.body()
                if (body == null) {
                    Resource.Error("Ответ без тела")
                } else {
                    val contentDisposition = response.headers()["Content-Disposition"]
                    val fileName = parseFileName(contentDisposition, fileUrl)
                    Resource.Success(body to fileName)
                }
            },
            onFailure = { e ->
                Resource.Error("Ошибка загрузки файла: ${e.message}")
            }
        ))
    }.flowOn(Dispatchers.IO)

    private fun parseFileName(contentDisposition: String?, fileUrl: String? = null): String? {
        // Try Content-Disposition first
        if (contentDisposition != null) {
            Regex("filename\\*=UTF-8''(.+)")
                .find(contentDisposition)
                ?.groupValues?.get(1)
                ?.let { return URLDecoder.decode(it, "UTF-8") }

            Regex("filename=\"?([^\";]+)\"?")
                .find(contentDisposition)
                ?.groupValues?.get(1)
                ?.let { return it }
        }

        return fileUrl?.substringAfterLast('/')
    }


}