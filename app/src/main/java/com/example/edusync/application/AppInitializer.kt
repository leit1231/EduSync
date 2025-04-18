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
        try {
            (instituteRepository as InstituteRepositoryImpl).syncInstitutes()
            val institutes = instituteRepository.getAllInstitutes().getOrNull() ?: emptyList()
            institutes.forEach {
                try {
                    (groupRepository as GroupRepositoryImpl).syncGroups(it.id)
                } catch (e: Exception) {
                    println("Failed to sync groups for institute ${it.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            println("Failed to initialize app: ${e.message}")
        }
    }
}