package com.example.edusync.presentation.viewModels.materials

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.materials.group.CreateGroupState
import kotlinx.coroutines.launch

class CreateGroupViewModel(private val navigator: Navigator): ViewModel() {

    private val _uiState = mutableStateOf(CreateGroupState())
    val uiState: State<CreateGroupState> = _uiState

    fun goBack(){
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }

    private val universityGroups = mapOf(
        "РКСИ" to listOf("ИС-11", "ИС-21", "ИС-31", "ИС-41", "ИС-12", "ИС-22")
    )

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
}