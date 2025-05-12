package com.example.edusync.presentation.viewModels.enterCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.domain.use_case.changePassword.VerifyResetCodeUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.enterCode.EnterScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnterCodeViewModel(
    private val navigator: Navigator,
    private val verifyResetCodeUseCase: VerifyResetCodeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EnterScreenState())
    val state: StateFlow<EnterScreenState> = _state

    fun onNumberEntered(number: Int?, index: Int) {
        val newCode = _state.value.code.toMutableList().apply {
            set(index, number)
        }
        val nextIndex = if (number != null) {
            newCode.indexOfFirst { it == null }.takeIf { it != -1 } ?: index
        } else index

        _state.update {
            it.copy(code = newCode, focusedIndex = nextIndex)
        }
    }

    fun onFocusChanged(index: Int) {
        _state.update { it.copy(focusedIndex = index) }
    }

    fun onBackspacePressed() {
        val currentFocus = _state.value.focusedIndex ?: return
        val newCode = _state.value.code.toMutableList().apply {
            set(currentFocus, null)
        }
        val previousIndex = (currentFocus - 1).coerceAtLeast(0)

        _state.update {
            it.copy(code = newCode, focusedIndex = previousIndex)
        }
    }

    fun goToChangePassword() {
        val code = state.value.code.joinToString("") { it?.toString() ?: "" }

        viewModelScope.launch {
            verifyResetCodeUseCase(code).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false) }
                        navigator.navigate(Destination.ChangePasswordScreen(code))
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, focusedIndex = 0) }
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