package com.example.edusync.data.repository.subject

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.SubjectResponse
import com.example.edusync.domain.repository.subject.SubjectRepository
import retrofit2.Response

class SubjectRepositoryImpl(
    private val api: EduSyncApiService,
    private val encryptedPrefs: EncryptedSharedPreference
) : SubjectRepository {

    override suspend fun getSubjectsByGroup(groupId: Int): Result<List<SubjectResponse>> {
        return executeWithToken { token ->
            api.getSubjectsByGroup(token, groupId)
        }
    }

    private suspend fun <T> executeWithToken(apiCall: suspend (String) -> Response<T>): Result<T> {
        val token = encryptedPrefs.getAccessToken() ?: return Result.failure(Exception("No token"))
        val response = apiCall(token)
        return if (response.isSuccessful) {
            response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty body"))
        } else {
            Result.failure(Exception("Error ${response.code()}"))
        }
    }
}
