package com.example.edusync.presentation.viewModels.changePasswordAfterForgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.domain.use_case.changePassword.ResetPasswordUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.changePasswordAfterForgot.ChangePasswordAfterForgotState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangePasswordAfterForgotViewModel(
    private val navigator: Navigator,
    private val code: String,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordAfterForgotState())
    val uiState: StateFlow<ChangePasswordAfterForgotState> = _uiState

    fun onNewPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(newPassword = newPassword, passwordError = "")
    }

    fun goToLogin() {

        val password = _uiState.value.newPassword

        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = "Введите новый пароль")
            return
        }

        viewModelScope.launch {
            resetPasswordUseCase(code, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(passwordError = "")
                    }
                    is Resource.Success -> {
                        navigator.navigate(
                            destination = Destination.AuthGraph,
                            navOptions = {
                                popUpTo(Destination.ForgotPasswordGraph) {
                                    inclusive = true
                                }
                            }
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            passwordError = result.message ?: "Ошибка смены пароля"
                        )
                    }
                }
            }
        }
    }

    fun goBack() {
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}