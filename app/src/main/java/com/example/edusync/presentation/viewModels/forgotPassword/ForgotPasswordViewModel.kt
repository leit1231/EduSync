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
        val error = if (newEmail.isBlank()) "Введите email" else null
        _viewState.value = _viewState.value?.copy(email = newEmail, emailError = error)
    }

    fun goToEnterCode() {
        val email = _viewState.value?.email.orEmpty()
        if (email.isBlank()) return

        viewModelScope.launch {
            requestPasswordResetUseCase(email).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _viewState.value = _viewState.value?.copy(emailError = null)
                    }
                    is Resource.Success -> {
                        navigator.navigate(Destination.EnterCodeScreen)
                    }
                    is Resource.Error -> {
                        _viewState.value = _viewState.value?.copy(emailError = result.message ?: "Ошибка запроса")
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