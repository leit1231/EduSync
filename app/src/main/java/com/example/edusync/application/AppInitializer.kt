package com.example.edusync.application

import com.example.edusync.data.repository.group.GroupRepositoryImpl
import com.example.edusync.data.repository.institution.InstituteRepositoryImpl
import com.example.edusync.domain.repository.group.GroupRepository
import com.example.edusync.domain.repository.institution.InstituteRepository

class AppInitializer(
    private val instituteRepository: InstituteRepository,
    private val groupRepository: GroupRepository
) {
    suspend fun initialize() {
        (instituteRepository as InstituteRepositoryImpl).syncInstitutes()
        val institutes = instituteRepository.getAllInstitutes().getOrNull() ?: emptyList()
        institutes.forEach {
            (groupRepository as GroupRepositoryImpl).syncGroups(it.id)
        }
    }
}