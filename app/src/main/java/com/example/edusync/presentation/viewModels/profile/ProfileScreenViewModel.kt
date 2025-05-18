package com.example.edusync.presentation.viewModels.profile

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.dto.UpdateProfileRequest
import com.example.edusync.domain.use_case.account.LogoutUseCase
import com.example.edusync.domain.use_case.account.UpdateProfileUseCase
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.domain.use_case.institution.GetAllInstitutesUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.profile.ProfileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileScreenViewModel(
    private val navigator: Navigator,
    private val logoutUseCase: LogoutUseCase,
    private val encryptedSharedPreference: EncryptedSharedPreference,
    private val getInstitutesUseCase: GetAllInstitutesUseCase,
    private val getGroupsUseCase: GetGroupsByInstitutionIdUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf(ProfileState())
    val uiState: State<ProfileState> = _uiState

    private var institutionIdMap = emptyMap<Int, String>()
    private var groupIdMap = emptyMap<Int, String>()

    private val _isLogoutDialogVisible = mutableStateOf(false)
    val isLogoutDialogVisible: State<Boolean> = _isLogoutDialogVisible

    var expandedUniversity by mutableStateOf(false)
    var expandedGroup by mutableStateOf(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            encryptedSharedPreference.getUser()
        }
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val user = encryptedSharedPreference.getUser()
            val fullName = user?.fullName?.split(" ") ?: listOf("", "", "")
            val originalSurname = fullName.getOrElse(0) { "" }
            val originalName = fullName.getOrElse(1) { "" }
            val originalPatronymic = fullName.getOrElse(2) { "" }

            _uiState.value = _uiState.value.copy(
                surname = originalSurname,
                name = originalName,
                patronymic = originalPatronymic,
                originalSurname = originalSurname,
                originalName = originalName,
                originalPatronymic = originalPatronymic,
                isTeacher = user?.isTeacher ?: false
            )

            if (user != null) {
                loadInstitutes(user.institutionId, user.groupId)
            }
        }
    }

    private fun loadInstitutes(institutionId: Int, groupId: Int) {
        viewModelScope.launch {
            getInstitutesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val institutes = resource.data ?: emptyList()
                        institutionIdMap = institutes.associateBy({ it.id }, { it.getDisplayName() })
                        val universityName = institutionIdMap[institutionId] ?: ""

                        _uiState.value = _uiState.value.copy(
                            availableUniversities = institutes.map { it.getDisplayName() },
                            selectedUniversity = universityName,
                            originalUniversity = universityName
                        )

                        loadGroups(institutionId, groupId)
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            availableUniversities = emptyList(),
                            selectedUniversity = "Ошибка загрузки"
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun loadGroups(institutionId: Int, groupId: Int) {
        viewModelScope.launch {
            getGroupsUseCase(institutionId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val groups = resource.data ?: emptyList()
                        groupIdMap = groups.associateBy({ it.id }, { it.name })
                        val groupName = groupIdMap[groupId].orEmpty()

                        _uiState.value = _uiState.value.copy(
                            availableGroups = groups.map { it.name },
                            selectedGroup = groupName,
                            originalGroup = groupName
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            availableGroups = emptyList(),
                            selectedGroup = "Ошибка загрузки"
                        )
                    }

                    else -> Unit
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

    fun onUniversitySelected(displayName: String) {
        viewModelScope.launch {
            val institutionId = institutionIdMap.entries.find { it.value == displayName }?.key
                ?: return@launch

            _uiState.value = _uiState.value.copy(
                selectedUniversity = displayName,
                selectedGroup = "",
                availableGroups = emptyList()
            )

            getGroupsUseCase(institutionId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val groups = resource.data ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            availableGroups = groups.map { it.name }
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    fun onGroupSelected(newValue: String) {
        _uiState.value = _uiState.value.copy(selectedGroup = newValue)
    }

    fun saveChanges() {
        viewModelScope.launch {
            val user = encryptedSharedPreference.getUser() ?: return@launch

            val fullName = "${_uiState.value.surname} ${_uiState.value.name} ${_uiState.value.patronymic}"
            val institutionId = institutionIdMap.entries.find { it.value == _uiState.value.selectedUniversity }?.key
                ?: return@launch
            val groupId = if (user.isTeacher) 0 else groupIdMap.entries.find { it.value == _uiState.value.selectedGroup }?.key
                ?: return@launch

            updateProfileUseCase(
                UpdateProfileRequest(
                    full_name = fullName,
                    institution_id = institutionId,
                    group_id = groupId
                )
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        encryptedSharedPreference.saveUser(
                            user.copy(
                                fullName = fullName,
                                institutionId = institutionId,
                                groupId = groupId
                            )
                        )
                        loadUserData()
                    }

                    is Resource.Error -> {
                        Log.e("UpdateProfile", "Ошибка обновления: ${result.message}")
                    }

                    else -> Unit
                }
            }
        }
    }

    fun performLogout() {
        viewModelScope.launch {
            logoutUseCase().collect {
                encryptedSharedPreference.clearUserData()
                goToLogin()
                hideLogoutDialog()
            }
        }
    }

    fun showLogoutDialog() {
        _isLogoutDialogVisible.value = true
    }

    fun hideLogoutDialog() {
        _isLogoutDialogVisible.value = false
    }

    fun goToSettings() {
        viewModelScope.launch {
            navigator.navigate(destination = Destination.SettingsScreen)
        }
    }

    private fun goToLogin() {
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.AuthGraph,
                navOptions = {
                    popUpTo(Destination.MainGraph) { inclusive = true }
                }
            )
        }
    }
}