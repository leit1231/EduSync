package com.example.edusync.domain.use_case.subject

import com.example.edusync.data.remote.dto.SubjectResponse
import com.example.edusync.domain.repository.subject.SubjectRepository

class GetSubjectsByGroupUseCase(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(groupId: Int): Result<List<SubjectResponse>> {
        return repository.getSubjectsByGroup(groupId)
    }
}
