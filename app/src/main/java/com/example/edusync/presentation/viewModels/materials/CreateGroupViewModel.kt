package com.example.edusync.presentation.viewModels.materials

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.edusync.presentation.views.materials.group.CreateGroupState

class CreateGroupViewModel: ViewModel() {
    private val _uiState = mutableStateOf(CreateGroupState())
    val uiState: State<CreateGroupState> = _uiState

    private val universityGroups = mapOf(
        "РКСИ" to listOf("ИС-11", "ИС-21", "ИС-31", "ИС-41", "ИС-12", "ИС-22")
    )

    var expandedUniversity by mutableStateOf(false)
    var expandedGroup by mutableStateOf(false)

    val availableGroups: List<String>
        get() = universityGroups["РКСИ"] ?: emptyList()


    fun onTitleLessonChange(newValue: String) {
        _uiState.value = _uiState.value.copy(titleLesson = newValue)
    }

    fun onNumbersOfHoursChange(newValue: String) {
        _uiState.value = _uiState.value.copy(numberOfHours = newValue)
    }


    fun onGroupSelected(newValue: String) {
        _uiState.value = _uiState.value.copy(selectedGroup = newValue)
    }

    fun onSave() {
        // TODO: Добавить логику сохранения (БД, API и т.д.)
    }
}