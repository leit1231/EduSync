package com.example.edusync.presentation.viewModels.enterCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.views.enterCode.EnterScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnterCodeViewModel() : ViewModel() {

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

    fun sendCode(code: String) {
        viewModelScope.launch {
            try {
            } catch (e: Exception) {
            }
        }
    }
}