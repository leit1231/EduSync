package com.example.edusync.presentation.viewModels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.domain.use_case.account.GetProfileUseCase
import com.example.edusync.domain.use_case.account.LoginUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.login.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val navigator: Navigator,
    private val loginUseCase: LoginUseCase,
    private val getUserInfo: GetProfileUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = validateEmail(email)) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = validatePassword(password)) }
    }

    fun onLoginClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            loginUseCase(_uiState.value.email, _uiState.value.password).collect { resource ->
                _uiState.update { it.copy(isLoading = resource is Resource.Loading) }
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let {
                            viewModelScope.launch {
                                getUserInfo().collect { profileResource ->
                                    if (profileResource is Resource.Success) {
                                        goToMainScreen()
                                    } else if (profileResource is Resource.Error) {
                                        _uiState.update { it.copy(error = profileResource.message) }
                                    }
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                generalError = resource.message,
                                isLoading = false
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = "^[a-zA-Z0-9._%+-]{3,}@[a-zA-Z0-9.-]{3,}\\.[a-zA-Z]{2,}\$".toRegex()

        return when {
            email.isEmpty() -> "Поле не должно быть пустым!"
            email.startsWith(' ') -> "Поле не должно начинаться с пробела"
            email.firstOrNull()?.isDigit() == true -> "Email не должен начинаться с цифры"
            !email.matches(emailRegex) -> "Некорректный формат email"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        val passwordRegex = "^(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])[A-Za-z0-9!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,30}\$".toRegex()

        return when {
            password.isEmpty() -> "Поле не должно быть пустым!"
            password.startsWith(' ') -> "Поле не должно начинаться с пробела"
            !password.matches(passwordRegex) -> "Пароль должен содержать 8-30 символов, включая цифры, спецсимволы и латинские буквы"
            else -> null
        }
    }

    fun goToForgotPassword(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.ForgotPasswordGraph
            )
        }
    }

    private suspend fun goToMainScreen(){
        withContext(Dispatchers.Main.immediate) {
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