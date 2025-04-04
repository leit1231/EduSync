package com.example.edusync.presentation.viewModels.forgotPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.forgotPassword.ForgotPasswordState
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val navigator: Navigator) : ViewModel() {
    private val _viewState = MutableLiveData(ForgotPasswordState())
    val viewState: LiveData<ForgotPasswordState> = _viewState

    fun onEmailChanged(newEmail: String) {
        val error = if (newEmail.isBlank()) "Введите email" else null
        _viewState.value = _viewState.value?.copy(
            email = newEmail,
            emailError = error
        )
    }

    fun goToEnterCode(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.EnterCodeScreen
            )
        }
    }

    fun goBack(){
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}