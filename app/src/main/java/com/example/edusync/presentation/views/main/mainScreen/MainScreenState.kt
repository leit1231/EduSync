package com.example.edusync.presentation.views.main.mainScreen

import com.example.edusync.common.LoadingState
import com.example.edusync.presentation.views.main.shedule.Schedule

data class MainScreenState(
    val selectedGroup: String? = null,
    val schedule: Schedule? = null,
    val scheduleLoading: LoadingState = LoadingState.Success,
    val textFieldValue: String = "",
    val groups: List<String>? = null,
    val groupsLoadingState: LoadingState = LoadingState.Success,
    val isEditMode: Boolean = false,
)