package com.example.edusync.presentation.viewModels.changePasswordAfterForgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.changePasswordAfterForgot.ChangePasswordAfterForgotState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangePasswordAfterForgotViewModel(private val navigator: Navigator) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordAfterForgotState())
    val uiState: StateFlow<ChangePasswordAfterForgotState> = _uiState

    fun onNewPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(newPassword = newPassword)
    }

    fun goToLogin(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.AuthGraph,
                navOptions = {
                    popUpTo(Destination.ForgotPasswordGraph){
                        inclusive = true
                    }
                }
            )
        }
    }

    fun goBack(){
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}