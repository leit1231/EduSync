package com.example.edusync.presentation.viewModels.confirmEmail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.confirmEmail.ConfirmEmailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfirmEmailViewModel(private val navigator: Navigator): ViewModel() {

    private val _state = MutableStateFlow(ConfirmEmailState())
    val state: StateFlow<ConfirmEmailState> = _state

    fun onNumberEntered(number: Int?, index: Int) {
        val newCode = _state.value.code.toMutableList().apply {
            set(index, number)
        }

        val nextIndex = if (number != null) {
            newCode.indexOfFirst { it == null }.takeIf { it != -1 } ?: index
        } else index

        _state.update {
            it.copy(
                code = newCode,
                focusedIndex = nextIndex
            )
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
            it.copy(
                code = newCode,
                focusedIndex = previousIndex
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

    fun goBack(){
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}