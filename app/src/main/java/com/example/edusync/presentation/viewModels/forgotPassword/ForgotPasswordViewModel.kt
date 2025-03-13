package com.example.edusync.presentation.viewModels.forgotPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.edusync.presentation.views.forgotPassword.ForgotPasswordState

class ForgotPasswordViewModel : ViewModel() {
    private val _viewState = MutableLiveData(ForgotPasswordState())
    val viewState: LiveData<ForgotPasswordState> = _viewState

    fun onEmailChanged(newEmail: String) {
        val error = if (newEmail.isBlank()) "Введите email" else null
        _viewState.value = _viewState.value?.copy(
            email = newEmail,
            emailError = error
        )
    }
}