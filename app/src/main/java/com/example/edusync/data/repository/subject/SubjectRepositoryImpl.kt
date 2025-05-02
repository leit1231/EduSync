package com.example.edusync.data.repository.subject

import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.repository.subject.SubjectRepository

class SubjectRepositoryImpl(
    private val api: EduSyncApiService,
    prefs: EncryptedSharedPreference
) : SubjectRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun getSubjectsByGroup(groupId: Int) =
        executor.execute { api.getSubjectsByGroup(it, groupId) }
}

