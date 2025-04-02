package com.example.edusync.presentation.viewModels.changePasswordAfterForgot

import androidx.lifecycle.ViewModel
import com.example.edusync.presentation.views.changePasswordAfterForgot.ChangePasswordAfterForgotState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChangePasswordAfterForgotViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordAfterForgotState())
    val uiState: StateFlow<ChangePasswordAfterForgotState> = _uiState

    fun onNewPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(newPassword = newPassword)
    }
}