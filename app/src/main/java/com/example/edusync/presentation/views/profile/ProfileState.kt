package com.example.edusync.presentation.views.profile

data class ProfileState(
    val surname: String = "",
    val name: String = "",
    val patronymic: String = "",
    val selectedUniversity: String = "",
    val selectedGroup: String = "",
    val availableUniversities: List<String> = emptyList(),
    val availableGroups: List<String> = emptyList(),

    val originalSurname: String = "",
    val originalName: String = "",
    val originalPatronymic: String = "",
    val originalUniversity: String = "",
    val originalGroup: String = "",

    val isTeacher: Boolean = false
) {
    val isSaveEnabled: Boolean
        get() {
            val fioChanged = surname != originalSurname ||
                    name != originalName ||
                    patronymic != originalPatronymic

            val universityChanged = selectedUniversity != originalUniversity
            val groupChanged = selectedGroup != originalGroup

            val isGroupValid = isTeacher || selectedGroup.isNotBlank()

            val allFieldsValid = surname.isNotBlank() &&
                    name.isNotBlank() &&
                    patronymic.isNotBlank() &&
                    selectedUniversity.isNotBlank() &&
                    isGroupValid

            return allFieldsValid && (fioChanged || universityChanged || groupChanged)
        }
}
