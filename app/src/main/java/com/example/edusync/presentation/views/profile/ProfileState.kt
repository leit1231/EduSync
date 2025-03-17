package com.example.edusync.presentation.views.profile

data class ProfileState(
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