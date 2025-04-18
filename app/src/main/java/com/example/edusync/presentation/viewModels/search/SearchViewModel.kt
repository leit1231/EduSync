package com.example.edusync.presentation.viewModels.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getGroupsUseCase: GetGroupsByInstitutionIdUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchResults = MutableStateFlow<List<String>>(emptyList())
    val searchResults: StateFlow<List<String>> = _searchResults

    private var isTeacherMode = false
    private var currentInstitutionId: Int? = null

    fun setInitialData(isTeacher: Boolean, institutionId: Int?) {
        isTeacherMode = isTeacher
        currentInstitutionId = institutionId
        performSearch()
    }

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun performSearch() {
        viewModelScope.launch {
            _isSearching.value = true
            currentInstitutionId?.let { institutionId ->
                try {
                    _searchResults.value = when (val resource = getGroupsUseCase(institutionId).first()) {
                        is Resource.Success -> {
                            // Исправляем маппинг данных
                            resource.data?.map { it.name } ?: emptyList()
                        }
                        is Resource.Error -> {
                            Log.e("SearchScreen", resource.message.toString())
                            emptyList()
                        }
                        else -> emptyList()
                    }
                } finally {
                    _isSearching.value = false
                }
            }
        }
    }
}