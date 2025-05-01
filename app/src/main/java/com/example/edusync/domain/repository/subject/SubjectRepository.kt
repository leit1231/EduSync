package com.example.edusync.domain.repository.subject

import com.example.edusync.data.remote.dto.SubjectResponse

interface SubjectRepository {
    suspend fun getSubjectsByGroup(groupId: Int): Result<List<SubjectResponse>>
}
