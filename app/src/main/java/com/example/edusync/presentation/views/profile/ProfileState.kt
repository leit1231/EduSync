package com.example.edusync.presentation.views.profile

data class ProfileState(
    val surname: String = "",
    val name: String = "",
    val patronymic: String = "",
    val selectedUniversity: String = "",
    val selectedGroup: String = "",
    val availableUniversities: List<String> = emptyList(),
    val availableGroups: List<String> = emptyList(),
    val isDataChanged: Boolean = false
) {
    val isSaveEnabled: Boolean
        get() = isDataChanged && surname.isNotBlank() && name.isNotBlank() && patronymic.isNotBlank() &&
                selectedUniversity.isNotBlank() && selectedGroup.isNotBlank()
}