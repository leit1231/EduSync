package com.example.edusync.presentation.viewModels.infoStudent

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.domain.use_case.account.RegisterUseCase
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.domain.use_case.institution.GetAllInstitutesUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.infoScreen.InfoScreenState
import kotlinx.coroutines.launch

class InfoStudentViewModel(
    private val registerUseCase: RegisterUseCase,
    private val getInstitutesUseCase: GetAllInstitutesUseCase,
    private val getGroupsUseCase: GetGroupsByInstitutionIdUseCase,
    private val email: String,
    private val password: String,
    val role: Boolean,
    private val navigator: Navigator
): ViewModel() {

    private val _uiState = mutableStateOf(InfoScreenState())
    val uiState: State<InfoScreenState> = _uiState

    private var institutionIdMap = emptyMap<String, Int>()
    private var groupIdMap = emptyMap<String, Int>()

    var expandedUniversity by mutableStateOf(false)

    var expandedGroup by mutableStateOf(false)

    private var _surnameError = mutableStateOf<String?>(null)
    val surnameError: State<String?> = _surnameError

    private var _nameError = mutableStateOf<String?>(null)
    val nameError: State<String?> = _nameError

    private var _patronymicError = mutableStateOf<String?>(null)
    val patronymicError: State<String?> = _patronymicError

    init {
        loadInstitutes()
    }

    private fun loadInstitutes() {
        viewModelScope.launch {
            getInstitutesUseCase().collect { resource ->
                when(resource) {
                    is Resource.Success -> {
                        val institutes = resource.data ?: emptyList()
                        institutionIdMap = institutes.associate { it.getDisplayName() to it.id }
                        _uiState.value = _uiState.value.copy(
                            availableUniversities = institutes.map { it.getDisplayName() }
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(selectedUniversity = "Ошибка загрузки институтов")
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun onUniversitySelected(displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedUniversity = displayName,
                selectedGroup = "",
                availableGroups = emptyList()
            )
            expandedUniversity = false

            val institutionId = institutionIdMap[displayName] ?: return@launch
            getGroupsUseCase(institutionId).collect { resource ->
                when(resource) {
                    is Resource.Success -> {
                        val groups = resource.data ?: emptyList()
                        groupIdMap = groups.associate { it.name to it.id }
                        _uiState.value = _uiState.value.copy(
                            availableGroups = groups.map { it.name },
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = "Ошибка загрузки групп"
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun validateSurname() {
        _surnameError.value = fioValidation(_uiState.value.surname)
    }

    fun validateName() {
        _nameError.value = fioValidation(_uiState.value.name)
    }

    fun validatePatronymic() {
        _patronymicError.value = fioValidation(_uiState.value.patronymic)
    }

    fun registerUser() {
        validateSurname()
        validateName()
        validatePatronymic()
        if (surnameError.value != null || nameError.value != null || patronymicError.value != null) {
            return
        }
        val fullName = "${_uiState.value.surname} " +
                "${_uiState.value.name} " +
                _uiState.value.patronymic
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            registerUseCase(
                email = email,
                password = password,
                fullName = fullName,
                isTeacher = role,
                institutionId = institutionIdMap[_uiState.value.selectedUniversity] ?: 0,
                groupId = groupIdMap[_uiState.value.selectedGroup] ?: 0
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> goToConfirmEmailScreen()
                    is Resource.Error -> {
                        val errorMessage = resource.message ?: "Неизвестная ошибка"
                        val cleanMessage = errorMessage.replace("Failure(java.lang.Throwable: ", "")
                            .replace(")", "")
                        _uiState.value = _uiState.value.copy(
                            error = cleanMessage,
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun onSurnameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(surname = newValue)
    }

    fun onNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(name = newValue)
    }

    fun onPatronymicChange(newValue: String) {
        _uiState.value = _uiState.value.copy(patronymic = newValue)
    }

    fun onGroupSelected(newValue: String) {
        _uiState.value = _uiState.value.copy(selectedGroup = newValue)
    }

    private fun goToConfirmEmailScreen(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.ConfirmEmailScreen
            )
        }
    }

    private fun fioValidation(valid: String): String? {
        if (valid.isBlank()){
            return "Поле не должно быть пустым"
        }
        if (!valid.matches(Regex("[а-яА-ЯёЁa-zA-Z]+"))) {
            return "Поле должно содержать только буквы"
        }
        if (valid.startsWith("ъ", ignoreCase = true) || valid.startsWith("ь", ignoreCase = true) || valid.startsWith(" ")) {
            return "Поле не должно начинаться с ъ или ь"
        }
        return null
    }
}