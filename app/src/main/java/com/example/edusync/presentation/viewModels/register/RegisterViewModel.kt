package com.example.edusync.presentation.viewModels.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.register.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val navigator: Navigator) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun setEmail(email: String) {
        _state.update {
            it.copy(email = email, emailError = validateEmail(email))
        }
    }

    fun setPassword(password: String) {
        _state.update {
            it.copy(password = password, passwordError = validatePassword(password))
        }
    }

    fun setConfirmPassword(passwordConfirmation: String) {
        _state.update { state ->
            val passwordError = validatePasswordConfirmation(
                state.password,
                passwordConfirmation
            )
            state.copy(
                passwordConfirmation = passwordConfirmation,
                passwordConfirmationError = passwordError
            )
        }
    }


    private fun validatePasswordConfirmation(
        password: String,
        passwordConfirmation: String
    ): String? {
        return if (password != passwordConfirmation) {
            "Пароли не совпадают"
        } else if (passwordConfirmation.length < 8) {
            "Пароль должен быть не менее 8 символов"
        } else {
            null
        }
    }

    fun register() {
        if (validateAllFields()) {
            goToInfoScreen(
                state.value.email,
                state.value.password,
                state.value.role
            )
        }
    }

    private fun validateAllFields(): Boolean {
        val currentState = state.value
        var isValid = true

        if (currentState.emailError != null) isValid = false
        if (currentState.passwordError != null) isValid = false
        if (currentState.passwordConfirmationError != null) isValid = false

        if (currentState.password != currentState.passwordConfirmation) {
            _state.update { it.copy(passwordConfirmationError = "Пароли не совпадают") }
            isValid = false
        }

        return isValid
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

    fun setRole(role: Boolean) {
        _state.update { it.copy(role = role) }
    }

    fun goToLogin(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.LoginScreen
            )
        }
    }

    private fun goToInfoScreen(email: String, password: String, role: Boolean) {
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.InfoScreen(
                    email = email,
                    password = password,
                    role = role
                )
            )
        }
    }
}