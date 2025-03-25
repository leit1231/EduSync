package com.example.edusync.presentation.views.main

data class MainScreenState(
    val selectedGroup: String? = null,
    val schedule: Schedule? = null,
    val scheduleLoading: LoadingState = LoadingState.Success,
    val textFieldValue: String = "",
    val groups: List<String>? = null,
    val groupsLoadingState: LoadingState = LoadingState.Success,
    val isEditMode: Boolean = false,
)

sealed class LoadingState {
    data object Loading: LoadingState()
    data object Success: LoadingState()
    data object Empty: LoadingState()
    data class Error(val error: String): LoadingState()
}