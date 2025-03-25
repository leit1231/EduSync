package com.example.edusync.presentation.viewModels.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearch() {
        viewModelScope.launch {
            _isSearching.value = true
            _isSearching.value = false
        }
    }
}