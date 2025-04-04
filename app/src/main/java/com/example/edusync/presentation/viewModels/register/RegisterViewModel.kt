package com.example.edusync.presentation.viewModels.register

import android.util.Patterns
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

    fun setConfirmPassword(password: String) {
        _state.update {
            it.copy(password = password, passwordError = validatePassword(password))
        }
    }

    private fun validateEmail(email: String): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            "Invalid email"
        else null
    }
    private fun validatePassword(email: String): String? {
        return if (email.length < 8)
            "Invalid password"
        else null
    }

    fun setRole(role: String) {
        _state.update { it.copy(role = role) }
    }

    fun goToLogin(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.LoginScreen
            )
        }
    }

    fun goToInfoScreen(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.InfoScreen
            )
        }
    }
}