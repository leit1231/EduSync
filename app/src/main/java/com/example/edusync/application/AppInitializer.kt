package com.example.edusync.application

import android.util.Log
import com.example.edusync.data.repository.group.GroupRepositoryImpl
import com.example.edusync.data.repository.institution.InstituteRepositoryImpl
import com.example.edusync.data.repository.schedule.ScheduleRepositoryImpl
import com.example.edusync.domain.repository.group.GroupRepository
import com.example.edusync.domain.repository.institution.InstituteRepository
import com.example.edusync.domain.repository.schedule.ScheduleRepository

class AppInitializer(
    private val instituteRepository: InstituteRepository,
    private val groupRepository: GroupRepository,
    private val teacherInitials: ScheduleRepository
) {
    suspend fun initialize() {
        try {
            (instituteRepository as InstituteRepositoryImpl).syncInstitutes()
            val institutes = instituteRepository.getAllInstitutes().getOrNull() ?: emptyList()
            Log.d("AppInitializer", "Institutes: $institutes")
            institutes.forEach {
                try {
                    (groupRepository as GroupRepositoryImpl).syncGroups(it.id)
                } catch (e: Exception) {
                    Log.e("AppInitializer","Failed to sync groups for institute ${it.id}: ${e.message}")
                }
            }
            (teacherInitials as ScheduleRepositoryImpl).syncTeacherInitials()
        } catch (e: Exception) {
            Log.e("AppInitializer", "Failed to initialize app", e)
        }
    }
}