package com.example.edusync.presentation.viewModels.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getGroupsUseCase: GetGroupsByInstitutionIdUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchResults = MutableStateFlow<List<String>>(emptyList())

    private val _fullGroups = MutableStateFlow<List<Group>>(emptyList())
    val fullGroups: StateFlow<List<Group>> = _fullGroups
    private var allGroups: List<Group> = emptyList()

    private val _filteredGroups = MutableStateFlow<List<Group>>(emptyList())
    val filteredGroups: StateFlow<List<Group>> = _filteredGroups

    private var isTeacherMode = false
    private var currentInstitutionId: Int? = null

    init {
        viewModelScope.launch {
            combine(_searchQuery, _fullGroups) { query, groups ->
                if (query.isBlank()) {
                    groups
                } else {
                    groups.filter {
                        it.name.lowercase().startsWith(query.lowercase())
                    }
                }
            }.collect { _filteredGroups.value = it }
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
                    getGroupsUseCase(institutionId).collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                _fullGroups.value = resource.data ?: emptyList()
                                _searchResults.value = resource.data?.map { it.name } ?: emptyList()
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
                } finally {
                    _isSearching.value = false
                }
            }
        }
    }
}