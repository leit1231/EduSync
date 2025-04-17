package com.example.edusync.presentation.viewModels.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.domain.use_case.account.LogoutUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.profile.ProfileState
import kotlinx.coroutines.launch

class ProfileScreenViewModel(
    private val navigator: Navigator,
    private val logoutUseCase: LogoutUseCase,
    private val encryptedSharedPreference: EncryptedSharedPreference
): ViewModel() {
    private val _uiState = mutableStateOf(ProfileState())
    val uiState: State<ProfileState> = _uiState

    private val _isLogoutDialogVisible = mutableStateOf(false)
    val isLogoutDialogVisible: State<Boolean> = _isLogoutDialogVisible

    private val universityGroups = mapOf(
        "РКСИ" to listOf("ИС-11", "ИС-21", "ИС-31", "ИС-41", "ИС-12", "ИС-22"),
        "ДГТУ" to listOf("ДГТУ-1", "ДГТУ-2", "ДГТУ-3"),
        "РИНХ" to listOf("РИНХ-1", "РИНХ-2", "РИНХ-3")
    )

    fun performLogout() {
        viewModelScope.launch {
            logoutUseCase()
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            encryptedSharedPreference.clearTokens()
                            goToLogin()
                        }
                        is Resource.Error -> {
                            encryptedSharedPreference.clearTokens()
                            goToLogin()
                            hideLogoutDialog()
                        }
                        else -> {}
                    }
                }
        }
    }

    fun goToSettings(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.SettingsScreen
            )
        }
    }

    fun showLogoutDialog() {
        _isLogoutDialogVisible.value = true
    }

    fun hideLogoutDialog() {
        _isLogoutDialogVisible.value = false
    }

    private fun goToLogin() {
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.AuthGraph,
                navOptions = {
                    popUpTo(Destination.MainGraph) {
                        inclusive = true
                    }
                }
            )
        }
    }

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
}