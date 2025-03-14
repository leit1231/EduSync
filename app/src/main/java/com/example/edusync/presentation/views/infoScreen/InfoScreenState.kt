package com.example.edusync.presentation.views.infoScreen

data class InfoScreenState(
    val surname: String = "",
    val name: String = "",
    val patronymic: String = "",
    val selectedUniversity: String = "",
    val selectedGroup: String = "",
) {
    val isSaveEnabled: Boolean
        get() = surname.isNotBlank() && name.isNotBlank() && patronymic.isNotBlank() &&
                selectedUniversity.isNotBlank() && selectedGroup.isNotBlank()
}