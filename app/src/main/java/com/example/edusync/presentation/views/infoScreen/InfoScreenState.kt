package com.example.edusync.presentation.views.infoScreen

data class InfoScreenState(
    val surname: String = "",
    val name: String = "",
    val patronymic: String = "",
    val selectedUniversity: String = "",
    val selectedGroup: String = "",
    val availableUniversities: List<String> = emptyList(),
    val availableGroups: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val surnameError: String? = null,
    val nameError: String? = null,
    val patronymicError: String? = null,
)