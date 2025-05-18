package com.example.edusync.presentation.viewModels.forgotPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.domain.use_case.changePassword.RequestPasswordResetUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.forgotPassword.ForgotPasswordState
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val navigator: Navigator,
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData(ForgotPasswordState())
    val viewState: LiveData<ForgotPasswordState> = _viewState

    fun onEmailChanged(newEmail: String) {
        val error = validateEmail(newEmail)
        _viewState.value = _viewState.value?.copy(
            email = newEmail,
            emailError = error,
            generalError = null
        )
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

    fun goToEnterCode() {
        val currentState = _viewState.value ?: return
        val emailError = validateEmail(currentState.email)
        if (emailError != null) {
            _viewState.value = currentState.copy(emailError = emailError)
            return
        }

        viewModelScope.launch {
            requestPasswordResetUseCase(currentState.email).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _viewState.value = _viewState.value?.copy(
                            emailError = null,
                            generalError = null
                        )
                    }
                    is Resource.Success -> {
                        navigator.navigate(Destination.EnterCodeScreen)
                    }
                    is Resource.Error -> {
                        _viewState.value = _viewState.value?.copy(
                            generalError = result.message
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