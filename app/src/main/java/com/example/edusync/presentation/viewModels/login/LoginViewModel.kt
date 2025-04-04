package com.example.edusync.presentation.viewModels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.login.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val navigator: Navigator) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun goToForgotPassword(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.ForgotPasswordGraph
            )
        }
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

    fun goToRegisterScreen(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.RegisterScreen
            )
        }
    }
}