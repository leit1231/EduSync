package com.example.edusync.presentation.viewModels.infoStudent

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.infoScreen.InfoScreenState
import kotlinx.coroutines.launch

class InfoStudentViewModel(private val navigator: Navigator): ViewModel() {
    private val _uiState = mutableStateOf(InfoScreenState())
    val uiState: State<InfoScreenState> = _uiState

    private val universityGroups = mapOf(
        "РКСИ" to listOf("ИС-11", "ИС-21", "ИС-31", "ИС-41", "ИС-12", "ИС-22"),
        "ДГТУ" to listOf("ДГТУ-1", "ДГТУ-2", "ДГТУ-3"),
        "РИНХ" to listOf("РИНХ-1", "РИНХ-2", "РИНХ-3")
    )

    var expandedUniversity by mutableStateOf(false)
    var expandedGroup by mutableStateOf(false)

    val availableGroups: List<String>
        get() = universityGroups[_uiState.value.selectedUniversity].orEmpty()


    fun onSurnameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(surname = newValue)
    }

    fun onNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(name = newValue)
    }

    fun onPatronymicChange(newValue: String) {
        _uiState.value = _uiState.value.copy(patronymic = newValue)
    }

    fun onUniversitySelected(newValue: String) {
        _uiState.value = _uiState.value.copy(
            selectedUniversity = newValue,
            selectedGroup = ""
        )
    }

    fun onGroupSelected(newValue: String) {
        _uiState.value = _uiState.value.copy(selectedGroup = newValue)
    }

    fun goToMainScreen(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.MainGraph,
                navOptions = {
                    popUpTo(Destination.AuthGraph){
                        inclusive = true
                    }
                }
            )
        }
    }
}