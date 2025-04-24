package com.example.edusync.presentation.viewModels.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.domain.use_case.teachers.GetTeacherInitialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getGroupsUseCase: GetGroupsByInstitutionIdUseCase,
    private val getTeacherInitialsUseCase: GetTeacherInitialsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchResults = MutableStateFlow<List<String>>(emptyList())

    private val _fullGroups = MutableStateFlow<List<Group>>(emptyList())
    private var allGroups: List<Group> = emptyList()

    private val _fullTeachers = MutableStateFlow<List<TeacherInitialsResponse>>(emptyList())

    private val _filteredGroups = MutableStateFlow<List<Group>>(emptyList())
    val filteredGroups: StateFlow<List<Group>> = _filteredGroups

    private val _filteredTeachers = MutableStateFlow<List<TeacherInitialsResponse>>(emptyList())
    val filteredTeachers: StateFlow<List<TeacherInitialsResponse>> = _filteredTeachers

    var isTeacherMode = false
    private var currentInstitutionId: Int? = null

    init {
        viewModelScope.launch {
            combine(_searchQuery, _fullGroups, _fullTeachers) { query, groups, teachers ->
                if (query.isBlank()) {
                    if (isTeacherMode) teachers else groups
                } else {
                    if (isTeacherMode) {
                        teachers.filter {
                            it.initials.lowercase().startsWith(query.lowercase())
                        }
                    } else {
                        groups.filter {
                            it.name.lowercase().startsWith(query.lowercase())
                        }
                    }
                }
            }.collect { result ->
                if (isTeacherMode) {
                    _filteredTeachers.value = result as List<TeacherInitialsResponse>
                } else {
                    _filteredGroups.value = result as List<Group>
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setInitialData(isTeacher: Boolean, institutionId: Int?) {
        isTeacherMode = isTeacher
        currentInstitutionId = institutionId
        if (allGroups.isEmpty()) {
            performSearch()
        }
    }

    fun performSearch() {
        viewModelScope.launch {
            currentInstitutionId?.let { institutionId ->
                try {
                    if (isTeacherMode) {
                        getTeacherInitialsUseCase().collect { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    _fullTeachers.value = resource.data ?: emptyList()
                                }
                                is Resource.Error -> {
                                    Log.e("SearchViewModel", "Error: ${resource.message}")
                                    _fullTeachers.value = emptyList()
                                    _searchResults.value = emptyList()
                                }
                                is Resource.Loading -> {
                                    _isSearching.value = true
                                }
                            }
                        }
                    } else {
                        getGroupsUseCase(institutionId).collect { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    _fullGroups.value = resource.data ?: emptyList()
                                    _searchResults.value =
                                        resource.data?.map { it.name } ?: emptyList()
                                }

                                is Resource.Error -> {
                                    Log.e("SearchViewModel", "Error: ${resource.message}")
                                    _fullGroups.value = emptyList()
                                    _searchResults.value = emptyList()
                                }

                                is Resource.Loading -> {
                                    _isSearching.value = true
                                }
                            }
                        }
                    }
                }finally {
                    _isSearching.value = false
                }
            }
        }
    }
}