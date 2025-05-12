package com.example.edusync.presentation.viewModels.confirmEmail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.domain.use_case.account.GetProfileUseCase
import com.example.edusync.domain.use_case.account.LoginUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.confirmEmail.ConfirmEmailState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmEmailViewModel(
    val email: String,
    private val password: String,
    private val loginUseCase: LoginUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(ConfirmEmailState())
    val state: StateFlow<ConfirmEmailState> = _state

    fun goToMainScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            loginUseCase(email, password).collect { loginResult ->
                _state.update { it.copy(isLoading = loginResult is Resource.Loading) }

                when (loginResult) {
                    is Resource.Success -> {
                        loginResult.data?.let {
                            getProfileUseCase().collect { profileResult ->
                                when (profileResult) {
                                    is Resource.Success -> goToMainScreenInternal()
                                    is Resource.Error -> {
                                        _state.update { it.copy(isLoading = false) }
                                    }
                                    is Resource.Loading -> {
                                        _state.update { it.copy(isLoading = true) }
                                    }
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                    }

                    else -> {}
                }
            }
        }
    }

    private suspend fun goToMainScreenInternal() {
        withContext(Dispatchers.Main.immediate) {
            navigator.navigate(
                destination = Destination.MainGraph,
                navOptions = {
                    popUpTo(Destination.AuthGraph) {
                        inclusive = true
                    }
                }
            )
        }
    }
}